package org.golive.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.golive.bank.provider.dao.po.PayOrderPO;

@Mapper
public interface IPayOrderMapper extends BaseMapper<PayOrderPO> {
}
