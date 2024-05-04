package org.golive.id.generator.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.id.generator.provider.dao.mapper.IdGenerateMapper;
import org.golive.id.generator.provider.dao.po.IdGeneratePO;
import org.golive.id.generator.provider.service.IdGenerateService;
import org.golive.id.generator.provider.service.bo.LocalSeqIdBO;
import org.golive.id.generator.provider.service.bo.LocalUnSeqIdBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {

    @Resource
    private IdGenerateMapper idGenerateMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateServiceImpl.class);
    // 有序id的Map映射集合
    private static Map<Integer, LocalSeqIdBO> localSeqIdBOMap = new ConcurrentHashMap<>();
    // 无序id的Map映射集合
    private static Map<Integer, LocalUnSeqIdBO> localUnSeqIdBOMap = new ConcurrentHashMap<>();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("id-generate-thread-" + ThreadLocalRandom.current().nextInt(1000));
                    return thread;
                }
            });
    // 阈值,当id段剩余不到25%的时候进行抢占
    private static final float UPDATE_RATE = 0.75f;
    private static final int SEQ_ID = 1;
    private static Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    @Override
    public Long getUnSeqId(Integer id) {
        if (id == null) {  // 校验id是否合法
            LOGGER.error("[getSeqId] id is error, id is {}", id);
            return null;
        }
        LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdBOMap.get(id);
        if (localUnSeqIdBO == null) {
            LOGGER.error("[getUnSeqId] localUnSeqIdBO is null, id is {}", id);
            return null;
        }
        Long returnId = localUnSeqIdBO.getIdQueue().poll();
        if (returnId == null) {
            LOGGER.error("[getUnSeqId] returnId is null, id is {}", id);
            return null;
        }
        // 这里会出现的问题:当IdQueue里面已经使用到了一定的瓶颈期,当可申请id余量低于阈值时,需要触发二次刷新操作,把队列里面的id段提前做一个填充
        this.refreshLocalUnSeqId(localUnSeqIdBO);
        return returnId;
    }

    @Override
    public Long getSeqId(Integer id) {  // 该id映射了一种分布式id策略
        if (id == null) {  // 校验id是否合法
            LOGGER.error("[getSeqId] id is error, id is {}", id);
            return null;
        }
        LocalSeqIdBO localSeqIdBO = localSeqIdBOMap.get(id);
        if (localSeqIdBO == null) {
            LOGGER.error("[getSeqId] localSeqIdBO is null, id is {}", id);
            return null;
        }
        this.refreshLocalSeqId(localSeqIdBO);
        // 保护机制,当前id若大于阈值id段,就不再进行自增操作
        // 防止自增操作导致占用其他线程的id段
        long returnId = localSeqIdBO.getCurrentNum().getAndIncrement();  // 取出当前值,并自增
        if (returnId > localSeqIdBO.getNextThreshold()) {
            // 同步去刷新 or 返回null ?
            // 选用返回null,让它快速失败
            LOGGER.error("[getSeqId] id is over limit, id is {}", id);
            return null;
        }
        return returnId;
    }

    /*
     * 刷新本地有序id段
     * */
    private void refreshLocalSeqId(LocalSeqIdBO localSeqIdBO) {
        // 1.计算已经取了多少id
        long step = localSeqIdBO.getNextThreshold() - localSeqIdBO.getCurrentStart();
        // 2.判断当前id段是否已经使用超过 75%
        if (localSeqIdBO.getCurrentNum().get() - localSeqIdBO.getCurrentStart() > step * UPDATE_RATE) {
            Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
            if (semaphore == null) {
                LOGGER.error("semaphore is null, id is {}", localSeqIdBO.getId());
                return;
            }
            // 拦截操作
            boolean acquireStatus = semaphore.tryAcquire();
            // 若acquire不成功,则不会执行同步操作
            if (acquireStatus) {
                LOGGER.info("尝试开始进行本地id段的同步操作");
                // 异步进行同步id段操作
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBO.getId());
                            tryUpdateMySQLRecord(idGeneratePO);
                        } catch (Exception e) {
                            LOGGER.error("[refreshLocalSeqId] error is ", e);
                        } finally {
                            // 释放semaphore
                            semaphoreMap.get(localSeqIdBO.getId()).release();
                            LOGGER.info("本地有序id段的同步完成, id is {}", localSeqIdBO.getId());
                        }
                    }
                });
            }

            // 若判断到使用超过75%,则会同步执行,有很多网络IO,性能慢,可以如上采用异步调用
            // IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBO.getId());
            // tryUpdateMySQLRecord(idGeneratePO);

        }
    }

    /*
     * 刷新本地无序id段
     * */
    private void refreshLocalUnSeqId(LocalUnSeqIdBO localUnSeqIdBO) {
        long begin = localUnSeqIdBO.getCurrentStart();
        long end = localUnSeqIdBO.getNextThreshold();
        long remainSize = localUnSeqIdBO.getIdQueue().size();
        // end - begin 代表本地id段能存放多少id
        // 如果使用剩余空间不足25%,则进行刷新
        if ((end - begin) * 0.25 > remainSize) {
            Semaphore semaphore = semaphoreMap.get(localUnSeqIdBO.getId());
            if (semaphore == null) {
                LOGGER.error("semaphore is null, id is {}", localUnSeqIdBO.getId());
                return;
            }
            // 拦截操作
            boolean acquireStatus = semaphore.tryAcquire();
            // 若acquire不成功,则不会执行同步操作
            if (acquireStatus) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBO.getId());
                            tryUpdateMySQLRecord(idGeneratePO);
                        } catch (Exception e) {
                            LOGGER.error("[refreshLocalUnSeqId] error is ", e);
                        } finally {
                            semaphoreMap.get(localUnSeqIdBO.getId()).release();
                            LOGGER.info("本地无序id段同步完成, id is {}", localUnSeqIdBO.getId());
                        }
                    }
                });
            }
            // 以下实现无法解决多线程下,可能会多次触发本地id段刷新操作
            // IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBO.getId());
            // tryUpdateMySQLRecord(idGeneratePO);
            // LOGGER.info("无序id段同步完成, id is {}", localUnSeqIdBO.getId());
        }
    }

    /*
     * 因为类是放在spring容器中进行托管的,SpringBoot服务启动时,Spring容器里的Bean有个生命周期
     * 会在bean初始化的时候回调到afterPropertiesSet()这里,故在这里对localSeqIdBOMap进行初始化
     * 利用InitializingBean回调接口实现.
     * */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<IdGeneratePO> idGeneratePOList = idGenerateMapper.selectAll();
        for (IdGeneratePO idGeneratePO : idGeneratePOList) {
            LOGGER.info("服务刚启动,抢占新的id段");
            LOGGER.info("idGeneratePO的值为 {}", idGeneratePO);
            tryUpdateMySQLRecord(idGeneratePO);

            // 初始化时,根据不同的分布式任务,初始成不同的Semaphore
            // 初值为1,限制每次做异步更新时,只能有一个线程进入
            semaphoreMap.put(idGeneratePO.getId(), new Semaphore(1));
        }
    }

    /*
     * 更新mysql里面的分布式id的配置信息,占用相应的id段
     * 同步执行,会有很多网络IO,性能较慢
     * */
    private void tryUpdateMySQLRecord(IdGeneratePO idGeneratePO) {

        // 更新操作就代表了抢占id段,因为改变了版本号,初始化的时候就会有一次抢断
        int updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
        if (updateResult > 0) {
            localIdBOHandler(idGeneratePO);
            return;
        }

        // 由于是高并发操作,可能该进程更新的时候,其他进程已经将数据库的版本号进行了一个修改,导致更新失败
        // 若更新失败,则重新查询数据库,得到最新id和版本号version
        // 更新失败,重试机制,最多3次
        for (int i = 0; i < 3; i++) {
            idGeneratePO = idGenerateMapper.selectById(idGeneratePO.getId());
            updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
            if (updateResult > 0) {
                // LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
                // AtomicLong atomicLong = new AtomicLong(idGeneratePO.getCurrentStart());
                // localSeqIdBO.setId(idGeneratePO.getId());
                // localSeqIdBO.setCurrentNum(atomicLong);
                // localSeqIdBO.setCurrentStart(idGeneratePO.getCurrentStart());
                // localSeqIdBO.setNextThreshold(idGeneratePO.getNextThreshold());
                // localSeqIdBOMap.put(localSeqIdBO.getId(), localSeqIdBO);
                localIdBOHandler(idGeneratePO);
                return;
            }
        }
        throw new RuntimeException("表id段占用失败,竞争过于激烈,id is:" + idGeneratePO.getId());
    }

    /*
     * 专门处理将本地ID对象放入到Map中,并且进行初始化
     * */
    private void localIdBOHandler(IdGeneratePO idGeneratePO) {
        long currentStart = idGeneratePO.getCurrentStart();
        long nextThreshold = idGeneratePO.getNextThreshold();
        long currentNum = currentStart;
        if (idGeneratePO.getIsSeq() == SEQ_ID) {  // 当该is_seq字段为1时,代表是有序id的生成
            LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
            AtomicLong atomicLong = new AtomicLong(currentNum);
            localSeqIdBO.setId(idGeneratePO.getId());  // 当前id生成策略
            localSeqIdBO.setCurrentNum(atomicLong);  // 设置当前内存记录的有序id值
            localSeqIdBO.setCurrentStart(currentStart);  // 设置开始值
            localSeqIdBO.setNextThreshold(nextThreshold);  // 设置阈值
            localSeqIdBOMap.put(localSeqIdBO.getId(), localSeqIdBO);  // 放入到map(本地内存)中
        } else {
            LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
            localUnSeqIdBO.setCurrentStart(currentStart);
            localUnSeqIdBO.setNextThreshold(nextThreshold);
            localUnSeqIdBO.setId(idGeneratePO.getId());
            long begin = localUnSeqIdBO.getCurrentStart();
            long end = localUnSeqIdBO.getNextThreshold();
            List<Long> idList = new ArrayList<>();
            for (long i = begin; i < end; i++) {
                idList.add(i);
            }
            // 将本地id段提前打乱,然后放入到队列当中
            Collections.shuffle(idList);  // 随机打乱数据
            ConcurrentLinkedQueue<Long> idQueue = new ConcurrentLinkedQueue<>();
            idQueue.addAll(idList);
            localUnSeqIdBO.setIdQueue(idQueue);
            localUnSeqIdBOMap.put(localUnSeqIdBO.getId(), localUnSeqIdBO);
        }
    }
}
