package org.golive.bank.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.golive.bank.provider.dao.po.GoliveCurrencyAccountPO;

/**
 * 虚拟币账户mapper
 */
@Mapper
public interface IGoliveCurrencyAccountMapper extends BaseMapper<GoliveCurrencyAccountPO> {

    @Update("update t_golive_currency_account set current_balance = current_balance + #{num} where user_id = #{userId}")
    void incr(@Param("userId") long userId, @Param("num") int num);

    @Select("select current_balance from t_golive_currency_account where user_id = #{userId} and status = 1")
    Integer queryBalance(@Param("userId") long userId);

    @Update("update t_golive_currency_account set current_balance = current_balance - #{num} where user_id = #{userId}")
    void decr(@Param("userId") long userId, @Param("num") int num);
}
