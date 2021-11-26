package lu.my.mall.service;

import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;

import java.util.Date;

public interface logService {
    void insertLog(String url, String IP, String Param, Date time, String user, String function);

    void shopmanInsertLog(String url, String IP, String Param, Date time, String user, String function);

    void userInsertLog(String url, String ip, String param, Date time, String user, String function);

    PageResult findBuyList(PageQueryUtil pageUtil);

    PageResult countGoodsList(PageQueryUtil pageUtil);

    PageResult findUserLog(PageQueryUtil pageUtil);

    PageResult findShopmanLog(PageQueryUtil pageUtil);

    PageResult findAdminLog(PageQueryUtil pageUtil);
}
