
package lu.my.mall.service.impl;

import lu.my.mall.common.Constants;
import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.vo.MallShoppingCartItemVO;
import lu.my.mall.dao.MallGoodsMapper;
import lu.my.mall.dao.MallShoppingCartItemMapper;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.entity.MallShoppingCartItem;
import lu.my.mall.service.MallShoppingCartService;
import lu.my.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MallShoppingCartServiceImpl implements MallShoppingCartService {

    @Autowired
    private MallShoppingCartItemMapper MallShoppingCartItemMapper;

    @Autowired
    private MallGoodsMapper MallGoodsMapper;

    @Override
    public String saveMallCartItem(MallShoppingCartItem MallShoppingCartItem) {
        MallShoppingCartItem temp = MallShoppingCartItemMapper.selectByUserIdAndGoodsId(MallShoppingCartItem.getUserId(), MallShoppingCartItem.getGoodsId());
        if (temp != null) {
            temp.setGoodsCount(MallShoppingCartItem.getGoodsCount());
            return updateMallCartItem(temp);
        }
        MallGoods MallGoods = MallGoodsMapper.selectByPrimaryKey(MallShoppingCartItem.getGoodsId());
        if (MallGoods == null) {
            return "商品不存在";
        }
        int totalItem = MallShoppingCartItemMapper.selectCountByUserId(MallShoppingCartItem.getUserId()) + 1;
        if (MallShoppingCartItemMapper.insertSelective(MallShoppingCartItem) > 0) {
            return "成功";
        }
        return "失败";
    }

    @Override
    public String updateMallCartItem(MallShoppingCartItem MallShoppingCartItem) {
        MallShoppingCartItem MallShoppingCartItemUpdate = MallShoppingCartItemMapper.selectByPrimaryKey(MallShoppingCartItem.getCartItemId());
        if (MallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (MallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        MallShoppingCartItemUpdate.setGoodsCount(MallShoppingCartItem.getGoodsCount());
        MallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (MallShoppingCartItemMapper.updateByPrimaryKeySelective(MallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }
    @Override
    public MallShoppingCartItem getMallCartItemById(Long MallShoppingCartItemId) {
        return MallShoppingCartItemMapper.selectByPrimaryKey(MallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long MallShoppingCartItemId) {
        return MallShoppingCartItemMapper.deleteByPrimaryKey(MallShoppingCartItemId) > 0;
    }

    @Override
    public List<MallShoppingCartItemVO> getMyShoppingCartItems(Long MallUserId) {
        List<MallShoppingCartItemVO> MallShoppingCartItemVOS = new ArrayList<>();
        List<MallShoppingCartItem> MallShoppingCartItems = MallShoppingCartItemMapper.selectByUserId(MallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(MallShoppingCartItems)) {
            List<Long> MallGoodsIds = MallShoppingCartItems.stream().map(MallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<MallGoods> MallGoods = MallGoodsMapper.selectByPrimaryKeys(MallGoodsIds);
            Map<Long, MallGoods> MallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(MallGoods)) {
               MallGoodsMap = MallGoods.stream().collect(Collectors.toMap(lu.my.mall.entity.MallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        }
            for (MallShoppingCartItem MallShoppingCartItem : MallShoppingCartItems) {
                MallShoppingCartItemVO MallShoppingCartItemVO = new MallShoppingCartItemVO();
                BeanUtil.copyProperties(MallShoppingCartItem, MallShoppingCartItemVO);
                if (MallGoodsMap.containsKey(MallShoppingCartItem.getGoodsId())) {
                    MallGoods MallGoodsTemp = MallGoodsMap.get(MallShoppingCartItem.getGoodsId());
                    MallShoppingCartItemVO.setGoodsCoverImg(MallGoodsTemp.getGoodsCoverImg());
                    String goodsName = MallGoodsTemp.getGoodsName();
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    MallShoppingCartItemVO.setGoodsName(goodsName);
                    MallShoppingCartItemVO.setSellingPrice(MallGoodsTemp.getSellingPrice());
                    MallShoppingCartItemVOS.add(MallShoppingCartItemVO);
                }
            }
        }
        return MallShoppingCartItemVOS;
    }
}
