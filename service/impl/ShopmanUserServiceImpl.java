package lu.my.mall.service.impl;

import lu.my.mall.dao.AdminUserMapper;
import lu.my.mall.dao.ShopManMapper;
import lu.my.mall.entity.AdminUser;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.entity.ShopMan;
import lu.my.mall.service.ShopmanUserService;
import lu.my.mall.util.MD5Util;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;
import lu.my.mall.util.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ShopmanUserServiceImpl implements ShopmanUserService {
    @Resource
    private ShopManMapper shopManMapper;

    @Override
    public ShopMan login(String userName, String password) {
        return shopManMapper.login(userName, password);
    }

    @Override
    public ShopMan getUserDetailById(Integer loginUserId) {
        return shopManMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public PageResult findShopmanList(PageQueryUtil pageUtil) {
        List<ShopMan> shopmanList = shopManMapper.findShopmanList(pageUtil);
        int total = 10;
        PageResult pageResult = new PageResult(shopmanList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        ShopMan shopman = shopManMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (shopman != null) {
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            //比较原密码是否正确
            if (originalPasswordMd5.equals(shopman.getLoginPassword())) {
                //设置新密码并修改
                shopman.setLoginPassword(newPasswordMd5);
                if (shopManMapper.updateByPrimaryKeySelective(shopman) > 0) {
                    //修改成功则返回true
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        ShopMan adminUser = shopManMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            //设置新名称并修改
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if (shopManMapper.updateByPrimaryKeySelective(adminUser) > 0) {
                //修改成功则返回true
                return true;
            }
        }
        return false;
    }
}
