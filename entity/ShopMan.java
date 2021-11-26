package lu.my.mall.entity;

public class ShopMan {



    private Integer ShopManUserId;

    private String loginUserName;

    private String loginPassword;

    private String nickName;



    public Integer getShopManUserId() {
        return ShopManUserId;
    }

    public void setShopManUserId(Integer shopManUserId) {
        ShopManUserId = shopManUserId;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ShopManUserId=").append(ShopManUserId);
        sb.append(", loginUserName=").append(loginUserName);
        sb.append(", loginPassword=").append(loginPassword);
        sb.append(", nickName=").append(nickName);
        sb.append("]");
        return sb.toString();
    }
}