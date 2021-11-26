package lu.my.mall.entity;

import java.util.Date;

public class logEntity {
    private Integer logID;

    private String logUser;

    private String logIP;

    private String logURL;

    private String logFunction;

    private Date logTime;

    private String logParam;

    public Integer getLogID() {
        return logID;
    }

    public void setLogID(Integer logID) {
        this.logID = logID;
    }

    public String getLoginUser() {
        return logUser;
    }

    public void setLoginUser(String loginUser) {
        this.logUser = loginUser;
    }

    public String getLogIP() {
        return logIP;
    }

    public void setLogIP(String logIP) {
        this.logIP = logIP;
    }

    public String getLogURL() {
        return logURL;
    }

    public void setLogURL(String logURL) {
        this.logURL = logURL;
    }

    public String getLogFunction() {
        return logFunction;
    }

    public void setLogFunction(String logFunction) {
        this.logFunction = logFunction;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getLogParam() {
        return logParam;
    }

    public void setLogParam(String logParam) {
        this.logParam = logParam;
    }




}
