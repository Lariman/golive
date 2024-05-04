package org.golive.living.provider.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.golive.common.interfaces.dto.PageWrapper;
import org.golive.common.interfaces.enums.CommonStatusEum;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.golive.im.constants.AppIdEnum;
import org.golive.im.core.server.interfaces.dto.ImOfflineDTO;
import org.golive.im.core.server.interfaces.dto.ImOnlineDTO;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.golive.im.router.interfaces.rpc.ImRouterRpc;
import org.golive.living.interfaces.constants.LivingRoomTypeEnum;
import org.golive.living.interfaces.dto.LivingPkRespDTO;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;
import org.golive.living.provider.dao.mapper.LivingRoomMapper;
import org.golive.living.provider.dao.mapper.LivingRoomRecordMapper;
import org.golive.living.provider.dao.po.LivingRoomPO;
import org.golive.living.provider.dao.po.LivingRoomRecordPO;
import org.golive.living.provider.service.ILivingRoomService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LivingRoomServiceImpl implements ILivingRoomService {

    @Resource
    private LivingRoomMapper livingRoomMapper;
    @Resource
    private LivingRoomRecordMapper livingRoomRecordMapper;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImRouterRpc imRouterRpc;

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        Integer roomId = livingRoomReqDTO.getRoomId();
        Integer appId = livingRoomReqDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        // 增量扫描 0-100, 101-200, 201-300
        Cursor<Object> cursor = redisTemplate.opsForSet().scan(cacheKey, ScanOptions.scanOptions().match("*").count(100).build());
        List<Long> userIdList = new ArrayList<>();
        while(cursor.hasNext()){
            Long userId = (Long) cursor.next();
            userIdList.add(userId);
        }
        return userIdList;
    }

    @Override
    public LivingRoomRespDTO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getAnchorId, anchorId);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEum.VALID_STATUS);
        queryWrapper.last("limit 1");
        return ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper), LivingRoomRespDTO.class);
    }

    @Override
    public void userOfflineHandler(ImOfflineDTO imOfflineDTO) {
        Long userId = imOfflineDTO.getUserId();
        Integer roomId = imOfflineDTO.getRoomId();
        Integer appId = imOfflineDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        redisTemplate.opsForSet().remove(cacheKey, userId);
        // 监听pk主播下线行为
        LivingRoomReqDTO roomReqDTO = new LivingRoomReqDTO();
        roomReqDTO.setRoomId(imOfflineDTO.getRoomId());
        roomReqDTO.setPkObjId(imOfflineDTO.getUserId());
        this.offlinePk(roomReqDTO);
    }

    @Override
    public void userOnlineHandler(ImOnlineDTO imOnlineDTO) {
        Long userId = imOnlineDTO.getUserId();
        Integer roomId = imOnlineDTO.getRoomId();
        Integer appId = imOnlineDTO.getAppId();
        String cacheKey = cacheKeyBuilder.buildLivingRoomUserSet(roomId, appId);
        // 存到redis中的一个set集合中
        redisTemplate.opsForSet().add(cacheKey, userId);
        redisTemplate.expire(cacheKey, 12, TimeUnit.HOURS);
    }

    /**
     * 分页查询
     */
    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO){
        String cacheKey = cacheKeyBuilder.buildLivingRoomList(livingRoomReqDTO.getType());
        int page = livingRoomReqDTO.getPage();
        int pageSize = livingRoomReqDTO.getPageSize();
        long total = redisTemplate.opsForList().size(cacheKey);  // size()获取对应key的集合长度
        // range()获取指定下标间的值
        List<Object> resultList = redisTemplate.opsForList().range(cacheKey, (page - 1) * pageSize, (page * pageSize));
        PageWrapper<LivingRoomRespDTO> pageWrapper = new PageWrapper<>();
        if(CollectionUtils.isEmpty(resultList)){  // 若缓存未命中,则返回空
            pageWrapper.setList(Collections.emptyList());
            pageWrapper.setHasNext(false);
            return pageWrapper;
        }else{
            List<LivingRoomRespDTO> livingRoomReqDTOS = ConvertBeanUtils.convertList(resultList, LivingRoomRespDTO.class);
            pageWrapper.setList(livingRoomReqDTOS);
            pageWrapper.setHasNext(page * pageSize < total);
            return pageWrapper;
        }
    }

    @Override
    public List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type){
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.eq(LivingRoomPO::getType, type);
        // 按照时间倒序展示
        queryWrapper.orderByDesc(LivingRoomPO::getId);
        queryWrapper.last("limit 1000");
        return ConvertBeanUtils.convertList(livingRoomMapper.selectList(queryWrapper), LivingRoomRespDTO.class);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        // 查redis
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(roomId);
        LivingRoomRespDTO queryResult = (LivingRoomRespDTO) redisTemplate.opsForValue().get(cacheKey);
        if(queryResult != null){
            // 空值缓存
            if(queryResult.getId() == null){
                return null;
            }
            return queryResult;
        }
        // 查DB
        LambdaQueryWrapper<LivingRoomPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LivingRoomPO::getId, roomId);
        queryWrapper.eq(LivingRoomPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        queryResult = ConvertBeanUtils.convert(livingRoomMapper.selectOne(queryWrapper), LivingRoomRespDTO.class);
        // 若数据库中没有查到,则设置空值缓存,防止缓存击穿
        if(queryResult == null){
            redisTemplate.opsForValue().set(cacheKey, new LivingRoomRespDTO(), 1, TimeUnit.MINUTES);
            return null;
        }
        // 数据库查到后,回写数据到redis
        redisTemplate.opsForValue().set(cacheKey, queryResult, 30, TimeUnit.MINUTES);
        return queryResult;
    }

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomPO livingRoomPO = ConvertBeanUtils.convert(livingRoomReqDTO, LivingRoomPO.class);
        livingRoomPO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        livingRoomPO.setStartTime(new Date());
        livingRoomMapper.insert(livingRoomPO);
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(livingRoomPO.getId());
        // 防止之前有空值缓存,这里做移除操作
        redisTemplate.delete(cacheKey);
        return livingRoomPO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomRespDTO livingRoomRespDTO = this.queryByRoomId(livingRoomReqDTO.getRoomId());
        if(livingRoomRespDTO == null){
            return false;
        }
        if(!(livingRoomRespDTO.getAnchorId().equals(livingRoomReqDTO.getAnchorId()))){
            return false;
        }
        LivingRoomRecordPO livingRoomRecordPO = ConvertBeanUtils.convert(livingRoomRespDTO, LivingRoomRecordPO.class);
        livingRoomRecordPO.setEndTime(new Date());
        livingRoomRecordPO.setStatus(CommonStatusEum.INVALID_STATUS.getCode());
        livingRoomRecordMapper.insert(livingRoomRecordPO);
        livingRoomMapper.deleteById(livingRoomRespDTO.getId());
        // 移除掉直播间cache
        String cacheKey = cacheKeyBuilder.buildLivingRoomObj(livingRoomReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        LivingRoomRespDTO currentLivingRoom = this.queryByRoomId(livingRoomReqDTO.getRoomId());
        LivingPkRespDTO respDTO = new LivingPkRespDTO();
        respDTO.setOnlineStatus(false);
        if (currentLivingRoom.getAnchorId().equals(livingRoomReqDTO.getPkObjId())) {
            respDTO.setMsg("主播不可以连线参与pk");
            return respDTO;
        }
        String cacheKey = cacheKeyBuilder.buildLivingOnlinePk(livingRoomReqDTO.getRoomId());
        boolean tryOnline = redisTemplate.opsForValue().setIfAbsent(cacheKey, livingRoomReqDTO.getPkObjId(), 30, TimeUnit.HOURS);
        if (tryOnline) {
            List<Long> userIdList = this.queryUserIdByRoomId(livingRoomReqDTO);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pkObjId", livingRoomReqDTO.getPkObjId());
            jsonObject.put("pkObjAvatar", "https://picdm.sunbangyan.cn/2023/08/29/w2qq1k.jpeg");
            batchSendImMsg(userIdList, ImMsgBizCodeEnum.LIVING_ROOM_PK_ONLINE.getCode(), jsonObject);
            respDTO.setMsg("连线成功");
            respDTO.setOnlineStatus(false);
        } else {
            respDTO.setMsg("目前有人在线，请稍后再试");
        }
        return respDTO;
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        String cacheKey = cacheKeyBuilder.buildLivingOnlinePk(livingRoomReqDTO.getRoomId());
        return redisTemplate.delete(cacheKey);
    }

    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        String cacheKey = cacheKeyBuilder.buildLivingOnlinePk(roomId);
        Object userId = redisTemplate.opsForValue().get(cacheKey);
        return userId != null ? (long) userId : null;
    }

    private void batchSendImMsg(List<Long> userIdList, int bizCode, JSONObject jsonObject){
        List<ImMsgBody> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        imRouterRpc.batchSendMsg(imMsgBodies);
    }
}
