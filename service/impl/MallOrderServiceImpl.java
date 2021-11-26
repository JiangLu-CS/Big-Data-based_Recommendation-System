
package lu.my.mall.service.impl;

import lu.my.mall.common.*;
import lu.my.mall.controller.vo.*;
import lu.my.mall.dao.MallGoodsMapper;
import lu.my.mall.dao.MallOrderItemMapper;
import lu.my.mall.dao.MallOrderMapper;
import lu.my.mall.dao.MallShoppingCartItemMapper;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.entity.MallOrder;
import lu.my.mall.entity.MallOrderItem;
import lu.my.mall.entity.StockNumDTO;
import lu.my.mall.service.MallOrderService;
import lu.my.mall.util.BeanUtil;
import lu.my.mall.util.NumberUtil;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class MallOrderServiceImpl implements MallOrderService {

    @Autowired
    private MallOrderMapper MallOrderMapper;
    @Autowired
    private MallOrderItemMapper MallOrderItemMapper;
    @Autowired
    private MallShoppingCartItemMapper MallShoppingCartItemMapper;
    @Autowired
    private MallGoodsMapper MallGoodsMapper;

    @Override
    public PageResult getMallOrdersPage(PageQueryUtil pageUtil) {
        List<MallOrder> MallOrders = MallOrderMapper.findMallOrderList(pageUtil);
        int total = MallOrderMapper.getTotalMallOrders(pageUtil);
        PageResult pageResult = new PageResult(MallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(MallOrder MallOrder) {
        MallOrder temp = MallOrderMapper.selectByPrimaryKey(MallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(MallOrder.getTotalPrice());
            temp.setUserAddress(MallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (MallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = MallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder MallOrder : orders) {
                if (MallOrder.getIsDeleted() == 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                    continue;
                }
                if (MallOrder.getOrderStatus() != 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (MallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = MallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder MallOrder : orders) {
                if (MallOrder.getIsDeleted() == 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                    continue;
                }
                if (MallOrder.getOrderStatus() != 1 && MallOrder.getOrderStatus() != 2) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (MallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrder> orders = MallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrder MallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (MallOrder.getIsDeleted() == 1) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (MallOrder.getOrderStatus() == 4 || MallOrder.getOrderStatus() < 0) {
                    errorOrderNos += MallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (MallOrderMapper.closeOrder(Arrays.asList(ids), MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUserVO user, List<MallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(MallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(MallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<MallGoods> MallGoods = MallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<MallGoods> goodsListNotSelling = MallGoods.stream()
                .filter(MallGoodsTemp -> MallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            MallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, MallGoods> MallGoodsMap =
        MallGoods.stream().collect(Collectors.toMap(lu.my.mall.entity.MallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (MallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!MallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > MallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(MallGoods)) {
            if (MallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = MallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                MallOrder MallOrder = new MallOrder();
                MallOrder.setOrderNo(orderNo);
                MallOrder.setUserId(user.getUserId());
                MallOrder.setUserAddress(user.getAddress());
                //总价
                for (MallShoppingCartItemVO MallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += MallShoppingCartItemVO.getGoodsCount() * MallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                MallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (MallOrderMapper.insertSelective(MallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<MallOrderItem> MallOrderItems = new ArrayList<>();
                    for (MallShoppingCartItemVO MallShoppingCartItemVO : myShoppingCartItems) {
                        MallOrderItem MallOrderItem = new MallOrderItem();
                        //使用BeanUtil工具类将MallShoppingCartItemVO中的属性复制到MallOrderItem对象中
                        BeanUtil.copyProperties(MallShoppingCartItemVO, MallOrderItem);
                        //MallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        MallOrderItem.setOrderId(MallOrder.getOrderId());
                        MallOrderItems.add(MallOrderItem);
                    }
                    //保存至数据库
                    if (MallOrderItemMapper.insertBatch(MallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        MallOrder MallOrder = MallOrderMapper.selectByOrderNo(orderNo);
        if (MallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<MallOrderItem> orderItems = MallOrderItemMapper.selectByOrderId(MallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<MallOrderItemVO> MallOrderItemVOS = BeanUtil.copyList(orderItems, MallOrderItemVO.class);
                MallOrderDetailVO MallOrderDetailVO = new MallOrderDetailVO();
                BeanUtil.copyProperties(MallOrder, MallOrderDetailVO);
                MallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(MallOrderDetailVO.getOrderStatus()).getName());
                MallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(MallOrderDetailVO.getPayType()).getName());
                MallOrderDetailVO.setMallOrderItemVOS(MallOrderItemVOS);
                return MallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public MallOrder getMallOrderByOrderNo(String orderNo) {
        return MallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = MallOrderMapper.getTotalMallOrders(pageUtil);
        List<MallOrder> MallOrders = MallOrderMapper.findMallOrderList(pageUtil);
        List<MallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(MallOrders, MallOrderListVO.class);
            //设置订单状态中文显示值
            for (MallOrderListVO MallOrderListVO : orderListVOS) {
                MallOrderListVO.setOrderStatusString(MallOrderStatusEnum.getMallOrderStatusEnumByStatus(MallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = MallOrders.stream().map(MallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<MallOrderItem> orderItems = MallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<MallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(MallOrderItem::getOrderId));
                for (MallOrderListVO MallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(MallOrderListVO.getOrderId())) {
                        List<MallOrderItem> orderItemListTemp = itemByOrderIdMap.get(MallOrderListVO.getOrderId());
                        //将MallOrderItem对象列表转换成MallOrderItemVO对象列表
                        List<MallOrderItemVO> MallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, MallOrderItemVO.class);
                        MallOrderListVO.setMallOrderItemVOS(MallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        MallOrder MallOrder = MallOrderMapper.selectByOrderNo(orderNo);
        if (MallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (MallOrderMapper.closeOrder(Collections.singletonList(MallOrder.getOrderId()), MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        MallOrder MallOrder = MallOrderMapper.selectByOrderNo(orderNo);
        if (MallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            MallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            MallOrder.setUpdateTime(new Date());
            if (MallOrderMapper.updateByPrimaryKeySelective(MallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        MallOrder MallOrder = MallOrderMapper.selectByOrderNo(orderNo);
        if (MallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            MallOrder.setOrderStatus((byte) MallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            MallOrder.setPayType((byte) payType);
            MallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            MallOrder.setPayTime(new Date());
            MallOrder.setUpdateTime(new Date());
            if (MallOrderMapper.updateByPrimaryKeySelective(MallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<MallOrderItemVO> getOrderItems(Long id) {
        MallOrder MallOrder = MallOrderMapper.selectByPrimaryKey(id);
        if (MallOrder != null) {
            List<MallOrderItem> orderItems = MallOrderItemMapper.selectByOrderId(MallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<MallOrderItemVO> MallOrderItemVOS = BeanUtil.copyList(orderItems, MallOrderItemVO.class);
                return MallOrderItemVOS;
            }
        }
        return null;
    }
}