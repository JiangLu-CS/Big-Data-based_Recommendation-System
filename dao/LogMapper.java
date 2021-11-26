package lu.my.mall.dao;

import lu.my.mall.entity.goodsCount;
import lu.my.mall.entity.logEntity;
import lu.my.mall.util.PageQueryUtil;

import java.util.List;

public interface LogMapper {
    void insert(logEntity logEntity);

    void shopmanInsert(logEntity logEntity);

    void userInsert(logEntity logEntity);

    List<logEntity> findLogBuyList(PageQueryUtil pageUtil);

    List<goodsCount> goodsCountList(PageQueryUtil pageUtil);

    List<logEntity> findUserLog(PageQueryUtil pageUtil);

    List<logEntity> findShopmanLog(PageQueryUtil pageUtil);

    List<logEntity> findAdminLog(PageQueryUtil pageUtil);
}
