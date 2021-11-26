package lu.my.mall.entity;

import java.util.Date;

public class userWatchLog {
    int stoptime;

    public long getStoptime() {
        return stoptime;
    }

    public void setStoptime(int stoptime) {
        this.stoptime = stoptime;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    int user_id;
    int goods_id;
    Date time;
}
