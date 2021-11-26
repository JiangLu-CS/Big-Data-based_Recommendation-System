
package lu.my.mall.dao;

import lu.my.mall.entity.MallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(MallShoppingCartItem record);

    int insertSelective(MallShoppingCartItem record);

    MallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    MallShoppingCartItem selectByUserIdAndGoodsId(@Param("MallUserId") Long MallUserId, @Param("goodsId") Long goodsId);

    List<MallShoppingCartItem> selectByUserId(@Param("MallUserId") Long MallUserId, @Param("number") int number);

    int selectCountByUserId(Long MallUserId);

    int updateByPrimaryKeySelective(MallShoppingCartItem record);

    int updateByPrimaryKey(MallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}