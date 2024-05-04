package org.golive.gift.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.golive.gift.provider.dao.po.SkuStockInfoPO;

@Mapper
public interface SkuStockInfoMapper extends BaseMapper<SkuStockInfoPO> {

    @Update("update t_sku_stock_info set stock_num = stock_num - #{num} where sku_id = #{skuId} and stock_num - #{num} > 0 and version = #{version}")
    int decrStockNumBySkuId(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("version") Integer version);


}
