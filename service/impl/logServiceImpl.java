package lu.my.mall.service.impl;

import lu.my.mall.dao.LogMapper;
import lu.my.mall.entity.goodsCount;
import lu.my.mall.entity.logEntity;
import lu.my.mall.service.logService;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class logServiceImpl implements logService {

    @Resource
    private LogMapper logMapper;


    @Override
    public void insertLog(String url, String IP, String Param, Date time, String user, String function){
//String url, String IP, String Param, Date time, String user, String function
        logEntity logEntity = new logEntity();
        logEntity.setLogTime(time);
        logEntity.setLogParam(Param);
        logEntity.setLoginUser(user);
        logEntity.setLogFunction(function);
        logEntity.setLogIP(IP);
        logMapper.insert(logEntity);
    }

    @Override
    public void shopmanInsertLog(String url, String IP, String Param, Date time, String user, String function) {
        logEntity logEntity = new logEntity();
        logEntity.setLogTime(time);
        logEntity.setLogParam(Param);
        logEntity.setLoginUser(user);
        logEntity.setLogFunction(function);
        logEntity.setLogIP(IP);
        logMapper.shopmanInsert(logEntity);
    }

    @Override
    public void userInsertLog(String url, String IP, String Param, Date time, String user, String function) {
        logEntity logEntity = new logEntity();
        logEntity.setLogTime(time);
        logEntity.setLogParam(Param);
        logEntity.setLoginUser(user);
        logEntity.setLogFunction(function);
        logEntity.setLogIP(IP);
        logMapper.userInsert(logEntity);
    }

    @Override
    public PageResult findBuyList(PageQueryUtil pageUtil) {
        List<logEntity> LogList = logMapper.findLogBuyList(pageUtil);
        int total = 10;
        PageResult pageResult = new PageResult(LogList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult countGoodsList(PageQueryUtil pageUtil) {
        List<goodsCount> goodsCountList = logMapper.goodsCountList(pageUtil);
        int total = 10;
        PageResult pageResult = new PageResult(goodsCountList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult findUserLog(PageQueryUtil pageUtil) {
        List<logEntity> userLog = logMapper.findUserLog(pageUtil);
        int total = 10;
        PageResult pageResult = new PageResult(userLog, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult findShopmanLog(PageQueryUtil pageUtil) {
        List<logEntity> userLog = logMapper.findShopmanLog(pageUtil);
        int total = 10;
        PageResult pageResult = new PageResult(userLog, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult findAdminLog(PageQueryUtil pageUtil) {
        List<logEntity> userLog = logMapper.findAdminLog(pageUtil);
        int total = 10;
        PageResult pageResult = new PageResult(userLog, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

}
