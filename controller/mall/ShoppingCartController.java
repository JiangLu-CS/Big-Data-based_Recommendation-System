
package lu.my.mall.controller.mall;

import lu.my.mall.common.Constants;
import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.admin.aspect.userAction;
import lu.my.mall.controller.vo.MallShoppingCartItemVO;
import lu.my.mall.controller.vo.MallUserVO;
import lu.my.mall.entity.MallShoppingCartItem;
import lu.my.mall.service.MallShoppingCartService;
import lu.my.mall.service.logService;
import lu.my.mall.service.userWatchLogService;
import lu.my.mall.util.Result;
import lu.my.mall.util.ResultGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ShoppingCartController {
    private static Logger logger = Logger.getLogger(ShoppingCartController.class.getName());

    @Autowired
    private Jedis jedis;

    @Resource
    private MallShoppingCartService MallShoppingCartService;
    @Resource
    private userWatchLogService userWatchLogService;
    @Resource
    private logService logService;

    @userAction(value = "查看购物车")
    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {

        int userid = 0;
        HttpSession session = request.getSession();
        if(session.getAttribute("MallUser") != null){
            MallUserVO mallUserVO = new MallUserVO();
            mallUserVO = (MallUserVO)session.getAttribute("MallUser");
            userid = Math.toIntExact(mallUserVO.getUserId());
        }
        if(session.getAttribute("time") != null){
            long time = System.currentTimeMillis() - (long)session.getAttribute("time");
            int timee = (int)time;
            long goodsId = (long)session.getAttribute("goodsId");
            int goodId = (int)goodsId;
            userWatchLogService.userWatchLog(timee,userid, goodId);
            session.removeAttribute("time");
        }
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<MallShoppingCartItemVO> myShoppingCartItems = MallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(MallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (MallShoppingCartItemVO MallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += MallShoppingCartItemVO.getGoodsCount() * MallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }


    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveMallShoppingCartItem(@RequestBody MallShoppingCartItem MallShoppingCartItem,
                                                 HttpSession httpSession) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();

        String URL = request.getRequestURL().toString();
        String IP = request.getRemoteAddr();
        String function = "添加购物车";

        String userid = "";
        if(session.getAttribute("MallUser") != null){
            MallUserVO mallUserVO = new MallUserVO();
            mallUserVO = (MallUserVO)session.getAttribute("MallUser");
            userid = String.valueOf(Math.toIntExact(mallUserVO.getUserId()));
        }

        String param = String.valueOf(MallShoppingCartItem.getGoodsId());
        Date time = new Date();
        logService.userInsertLog(URL,  IP,  param,  time,  userid,  function);

        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String saveResult = MallShoppingCartService.saveMallCartItem(MallShoppingCartItem);
        //添加成功
        logger.info("PRODUCT_RATING_PREFIX" + ":" + user.getUserId() +"|"+ param +"|"+ 2 +"|"+ System.currentTimeMillis()/1000);
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        jedis.lpush("userId:" + user.getUserId(), param + ":" + 1);
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateMallShoppingCartItem(@RequestBody MallShoppingCartItem MallShoppingCartItem,
                                                   HttpSession httpSession) {

        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String updateResult = MallShoppingCartService.updateMallCartItem(MallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @userAction(value = "删除购物车")
    @DeleteMapping("/shop-cart/{MallShoppingCartItemId}")
    @ResponseBody
    public Result updateMallShoppingCartItem(@PathVariable("MallShoppingCartItemId") Long MallShoppingCartItemId,
                                                   HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = MallShoppingCartService.deleteById(MallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<MallShoppingCartItemVO> myShoppingCartItems = MallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (MallShoppingCartItemVO MallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += MallShoppingCartItemVO.getGoodsCount() * MallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
