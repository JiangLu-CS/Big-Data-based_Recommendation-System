package lu.my.mall.dao;

import lu.my.mall.entity.userWatchLog;
import lu.my.mall.util.PageQueryUtil;

import java.util.List;

public interface userWatchMapper {
    void insert(userWatchLog userWatchLog);

    List<userWatchLog> findLogList(PageQueryUtil pageUtil);
}
