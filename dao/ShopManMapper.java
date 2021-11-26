
package lu.my.mall.dao;

import lu.my.mall.entity.MallGoods;
import lu.my.mall.entity.ShopMan;
import lu.my.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopManMapper {
    int insert(ShopMan record);

    int insertSelective(ShopMan record);


    ShopMan login(@Param("userName") String userName, @Param("password") String password);

    ShopMan selectByPrimaryKey(Integer shopmanId);

    int updateByPrimaryKeySelective(ShopMan record);

    int updateByPrimaryKey(ShopMan record);

    List<ShopMan> findShopmanList(PageQueryUtil pageUtil);
}