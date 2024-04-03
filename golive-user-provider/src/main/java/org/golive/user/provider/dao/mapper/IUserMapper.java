package org.golive.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.golive.user.provider.dao.po.UserPO;

@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {
}
