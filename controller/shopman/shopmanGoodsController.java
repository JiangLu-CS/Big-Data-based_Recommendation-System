
package lu.my.mall.controller.shopman;

import lu.my.mall.common.Constants;
import lu.my.mall.common.MallCategoryLevelEnum;
import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.shopman.aspect.shopmanAction;
import lu.my.mall.entity.GoodsCategory;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.service.*;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.Result;
import lu.my.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**

 */
@Controller
@RequestMapping("/shopman")
public class shopmanGoodsController {

    @Resource
    private MallGoodsService MallGoodsService;
    @Resource
    private MallCategoryService MallCategoryService;
    @Resource
    private ShopmanUserService shopmanUserService;
    @Resource
    private userWatchLogService userWatchLogService;
    @Resource
    private  logService logService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "shopman/newbee_mall_goods";
    }

    @GetMapping("/watchlog")
    public String watchlogPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "shopman/watchlog";
    }
    @GetMapping("/userBuyLog")
    public String userBuyLogPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "shopman/userBuyLog";
    }
    @GetMapping("/countGoods")
    public String countGoodsPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "shopman/countGoods";
    }


    @RequestMapping(value = "/watchlog/list", method = RequestMethod.GET)
    @ResponseBody
    public Result watchlog(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(userWatchLogService.findLogList(pageUtil));
    }
    @RequestMapping(value = "/countGoods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result countGoods(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(logService.countGoodsList(pageUtil));
    }

    @RequestMapping(value = "/userBuyLog/buylist", method = RequestMethod.GET)
    @ResponseBody
    public Result watchlogbuy(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(logService.findBuyList(pageUtil));
    }




    @shopmanAction(value = "查看所有商品")
    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "shopman/newbee_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @shopmanAction(value = "修改商品")
    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        MallGoods MallGoods = MallGoodsService.getMallGoodsById(goodsId);
        if (MallGoods == null) {
            return "error/error_400";
        }
        if (MallGoods.getGoodsCategoryId() > 0) {
            if (MallGoods.getGoodsCategoryId() != null || MallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = MallCategoryService.getGoodsCategoryById(MallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = MallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = MallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (MallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = MallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", MallGoods);
        request.setAttribute("path", "goods-edit");
        return "shopman/newbee_mall_goods_edit";
    }

    /**
     * 列表
     */
    @shopmanAction(value = "查看所有商品")
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(MallGoodsService.getMallGoodsPage(pageUtil));
    }




    /**
     * 添加
     */
    @shopmanAction(value = "添加商品")
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody MallGoods MallGoods) {
        if (StringUtils.isEmpty(MallGoods.getGoodsName())
                || StringUtils.isEmpty(MallGoods.getGoodsIntro())
                || StringUtils.isEmpty(MallGoods.getTag())
                || Objects.isNull(MallGoods.getOriginalPrice())
                || Objects.isNull(MallGoods.getGoodsCategoryId())
                || Objects.isNull(MallGoods.getSellingPrice())
                || Objects.isNull(MallGoods.getStockNum())
                || Objects.isNull(MallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(MallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(MallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = MallGoodsService.saveMallGoods(MallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @shopmanAction(value = "修改商品")
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody MallGoods MallGoods) {
        if (Objects.isNull(MallGoods.getGoodsId())
                || StringUtils.isEmpty(MallGoods.getGoodsName())
                || StringUtils.isEmpty(MallGoods.getGoodsIntro())
                || StringUtils.isEmpty(MallGoods.getTag())
                || Objects.isNull(MallGoods.getOriginalPrice())
                || Objects.isNull(MallGoods.getSellingPrice())
                || Objects.isNull(MallGoods.getGoodsCategoryId())
                || Objects.isNull(MallGoods.getStockNum())
                || Objects.isNull(MallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(MallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(MallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = MallGoodsService.updateMallGoods(MallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @shopmanAction(value = "查看商品详情")
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        MallGoods goods = MallGoodsService.getMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @shopmanAction(value = "修改商品销售状态")
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (MallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }
}