
package lu.my.mall.controller.mall;

import lu.my.mall.common.Constants;
import lu.my.mall.common.MallException;
import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.admin.aspect.userAction;
import lu.my.mall.controller.vo.MallOrderDetailVO;
import lu.my.mall.controller.vo.MallShoppingCartItemVO;
import lu.my.mall.controller.vo.MallUserVO;
import lu.my.mall.entity.MallOrder;
import lu.my.mall.service.MallOrderService;
import lu.my.mall.service.MallShoppingCartService;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.Result;
import lu.my.mall.util.ResultGenerator;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    private static Logger logger = Logger.getLogger(OrderController.class.getName());
    @Resource
    private MallShoppingCartService MallShoppingCartService;
    @Resource
    private MallOrderService MallOrderService;
    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallOrderDetailVO orderDetailVO = MallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        if (orderDetailVO == null) {
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @userAction(value = "查看订单")
    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", MallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @userAction(value = "下单")
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<MallShoppingCartItemVO> myShoppingCartItems = MallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (StringUtils.isEmpty(user.getAddress().trim())) {
            MallException.fail("adress error");
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            MallException.fail("cart error");
        }
       //logger.info("PRODUCT_RATING_PREFIX" + ":" + user.getUserId() +"|" +"|"+ 3 +"|"+ System.currentTimeMillis()/1000);
        String saveOrderResult = MallOrderService.saveOrder(user, myShoppingCartItems);
        return "redirect:/orders/" + saveOrderResult;
    }

    @userAction(value = "取消订单")
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = MallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = MallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallOrder MallOrder = MallOrderService.getMallOrderByOrderNo(orderNo);
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", MallOrder.getTotalPrice());
        return "mall/pay-select";
    }
    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallOrder MallOrder = MallOrderService.getMallOrderByOrderNo(orderNo);
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", MallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }
    @userAction("支付成功")
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = MallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
