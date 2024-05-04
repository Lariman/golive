package org.golive.user.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;

import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.common.interfaces.enums.CommonStatusEum;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.common.interfaces.utils.DESUtils;
import org.golive.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.golive.id.generator.enums.IdTypeEnum;
import org.golive.id.generator.interfaces.IdGenerateRpc;
import org.golive.user.dto.UserDTO;
import org.golive.user.dto.UserLoginDTO;
import org.golive.user.dto.UserPhoneDTO;
import org.golive.user.provider.dao.mapper.IUserPhoneMapper;
import org.golive.user.provider.dao.po.UserPhonePO;
import org.golive.user.provider.service.IUserPhoneService;
import org.golive.user.provider.service.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserPhoneServiceImpl implements IUserPhoneService {

    @Resource
    private IUserPhoneMapper userPhoneMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private IUserService userService;
    @DubboReference
    private IdGenerateRpc idGenerateRpc;

    @Override
    public UserLoginDTO login(String phone) {
        // 1.参数格式校验
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        // 2.是否注册过
        UserPhoneDTO userPhoneDTO = this.queryByPhone(phone);
        // 如果注册过,创建token,返回userId
        if (userPhoneDTO != null) {
            // return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId(), createAndSaveLoginToken(userPhoneDTO.getUserId()));
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId());
        }
        // 如果没注册过,生成user信息, 插入手机记录,绑定userId
        return registerAndLogin(phone);
    }

    /**
     * 新用户注册
     *
     * @param phone
     */
    private UserLoginDTO registerAndLogin(String phone) {
        // 1.生成分布式用户id
        Long userId = idGenerateRpc.getUnSeqId(IdTypeEnum.USER_ID.getCode());
        // 封装UserDTO--新用户数据
        UserDTO userDTO = new UserDTO();
        userDTO.setNickName("golive用户-" + userId);
        userDTO.setUserId(userId);
        userService.insertOne(userDTO);  // 插入t_user表中
        // 新用户的手机号
        UserPhonePO userPhonePO = new UserPhonePO();
        userPhonePO.setUserId(userId);
        userPhonePO.setPhone(DESUtils.encrypt(phone));
        userPhonePO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        userPhoneMapper.insert(userPhonePO);  // 插入t_user_phone表中
        redisTemplate.delete(cacheKeyBuilder.buildUserPhoneObjKey(phone));
        // return UserLoginDTO.loginSuccess(userId, createAndSaveLoginToken(userId));
        return UserLoginDTO.loginSuccess(userId);

    }

    /**
     * 创建并且记录token
     *
     * @param userId
     * @return
     */
    // public String createAndSaveLoginToken(Long userId) {
    //     // 根据JDK默认的UUID生成token字符串
    //     String token = UUID.randomUUID().toString();
    //     // token -> xxx:xxx:token redis get userId
    //     redisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token), userId, 30, TimeUnit.DAYS);
    //     return token;
    // }

    /**
     * 根据手机号查询用户
     *
     * @param phone
     * @return
     */
    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        String redisKey = cacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        if (userPhoneDTO != null) {
            // 若id为空,则为空值缓存
            if (userPhoneDTO.getUserId() == null) {
                return null;
            }
            return userPhoneDTO;
        }
        userPhoneDTO = this.queryByPhoneFromDB(phone);
        if (userPhoneDTO != null) {
            userPhoneDTO.setPhone(DESUtils.decrypt(userPhoneDTO.getPhone()));
            redisTemplate.opsForValue().set(redisKey, userPhoneDTO, 30, TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        // 缓存击穿--利用空值缓存解决
        userPhoneDTO = new UserPhoneDTO();
        redisTemplate.opsForValue().set(redisKey, userPhoneDTO, 5, TimeUnit.MINUTES);
        return null;
    }

    /**
     * 根据用户id批量查询手机相关信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        if (userId == null || userId < 10000) {
            return Collections.emptyList();
        }
        String redisKey = cacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList = redisTemplate.opsForList().range(redisKey,0, -1);
        if (!CollectionUtils.isEmpty(userPhoneList)) {
            if(((UserPhoneDTO)userPhoneList.get(0)).getUserId() == null){
                // 若为空,则为空值缓存
                return Collections.emptyList();
            }
            return userPhoneList.stream().map(x -> (UserPhoneDTO) x).collect(Collectors.toList());
        }
        List<UserPhoneDTO> userPhoneDTOS = this.queryByUserIdFromDB(userId);
        if(!CollectionUtils.isEmpty(userPhoneDTOS)){
            userPhoneDTOS.stream().forEach(x -> x.setPhone(DESUtils.decrypt(x.getPhone())));
            redisTemplate.opsForList().leftPushAll(redisKey, userPhoneDTOS.toArray());
            redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        // 缓存击穿,空值缓存返回
        redisTemplate.opsForList().leftPushAll(redisKey, new UserPhoneDTO());
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        return Collections.emptyList();
    }

    /**
     * 根据用户id从DB查询记录
     *
     * @param userId
     * @return
     */
    private List<UserPhoneDTO> queryByUserIdFromDB(Long userId) {
        LambdaQueryWrapper<UserPhonePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPhonePO::getUserId, userId);
        queryWrapper.eq(UserPhonePO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return ConvertBeanUtils.convertList(userPhoneMapper.selectList(queryWrapper), UserPhoneDTO.class);
    }

    /**
     * 根据手机号从DB查询记录
     *
     * @param phone
     * @return
     */
    private UserPhoneDTO queryByPhoneFromDB(String phone) {
        LambdaQueryWrapper<UserPhonePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPhonePO::getPhone, DESUtils.encrypt(phone));
        queryWrapper.eq(UserPhonePO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return ConvertBeanUtils.convert(userPhoneMapper.selectOne(queryWrapper), UserPhoneDTO.class);
    }
}

