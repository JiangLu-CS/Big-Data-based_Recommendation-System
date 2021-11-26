
package lu.my.mall.controller.mall;

import lu.my.mall.common.Constants;
import lu.my.mall.common.MallException;
import lu.my.mall.controller.admin.aspect.userAction;
import lu.my.mall.controller.vo.MallGoodsDetailVO;
import lu.my.mall.controller.vo.MallUserVO;
import lu.my.mall.controller.vo.SearchPageCategoryVO;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.service.MallCategoryService;
import lu.my.mall.service.MallGoodsService;
import lu.my.mall.util.BeanUtil;
import lu.my.mall.util.PageQueryUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class GoodsController {
    private static Logger logger = Logger.getLogger(ShoppingCartController.class.getName());



    @Autowired
    private Jedis jedis;
    @Resource
    private MallGoodsService MallGoodsService;
    @Resource
    private MallCategoryService MallCategoryService;

    @userAction(value = "查询商品")
    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = MallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        params.put("goodsSellStatus", 0);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", MallGoodsService.searchMallGoods(pageUtil));
        return "mall/search";
    }

    @userAction(value = "查看商品详情")
    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        HttpSession session = request.getSession();
        session.setAttribute("time", System.currentTimeMillis());
        session.setAttribute("goodsId", goodsId);
        MallGoods goods = MallGoodsService.getMallGoodsById(goodsId);
        if (goods == null) {
            MallException.fail("no exist");
        }
        if (0 != goods.getGoodsSellStatus()) {
            MallException.fail("已经下架了");
        }
        MallGoodsDetailVO goodsDetailVO = new MallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        MallUserVO user = (MallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<MallGoods> recommend = MallGoodsService
                .getGoodsRecommendation
                        (goodsId);
        request.setAttribute("recommend", recommend);//推荐商品

        jedis.lpush("userId:" + user.getUserId(), goodsId + ":" + 1);
        logger.info("PRODUCT_RATING_PREFIX" + ":" + user.getUserId() +"|"+ goodsId +"|"+ 1 +"|"+ System.currentTimeMillis()/1000);
        return "mall/detail";
    }
}
