package lu.my.mall.service.impl;

import lu.my.mall.dao.userWatchMapper;
import lu.my.mall.entity.userWatchLog;
import lu.my.mall.service.userWatchLogService;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class userWatchLogImpl implements userWatchLogService {
    @Resource
    private userWatchMapper userWatchLogMapper;
    @Override
    public void userWatchLog(int time, int userid, int goodsId) {
        userWatchLog userWatchLog = new userWatchLog();
        userWatchLog.setGoods_id(goodsId);
        userWatchLog.setStoptime(time);
        userWatchLog.setUser_id(userid);
        userWatchLog.setTime(new Date());
        userWatchLogMapper.insert(userWatchLog);
    }

    @Override
    public PageResult findLogList(PageQueryUtil pageUtil) {
            List<userWatchLog> LogList = userWatchLogMapper.findLogList(pageUtil);
            int total = 10;
            PageResult pageResult = new PageResult(LogList, total, pageUtil.getLimit(), pageUtil.getPage());
            return pageResult;

    }
}
