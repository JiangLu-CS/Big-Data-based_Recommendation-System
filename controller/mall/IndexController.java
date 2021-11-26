
package lu.my.mall.controller.mall;

import lu.my.mall.common.Constants;
import lu.my.mall.common.IndexConfigTypeEnum;
import lu.my.mall.controller.vo.MallIndexCarouselVO;
import lu.my.mall.controller.vo.MallIndexCategoryVO;
import lu.my.mall.controller.vo.MallIndexConfigGoodsVO;
import lu.my.mall.controller.vo.MallUserVO;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private MallCarouselService MallCarouselService;

    @Resource
    private MallIndexConfigService MallIndexConfigService;

    @Resource
    private MallCategoryService MallCategoryService;

    @Resource
    private userWatchLogService userWatchLogService;

    @Resource
    private MallGoodsService mallGoodsService;


    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        int userid = 0;
        HttpSession session = request.getSession();
        if(session.getAttribute("MallUser") != null){
            MallUserVO mallUserVO = new MallUserVO();
            mallUserVO = (MallUserVO)session.getAttribute("MallUser");
            userid = Math.toIntExact(mallUserVO.getUserId());
        }
        if(request.getAttribute("time") != null){
            long time = System.currentTimeMillis() - (long)request.getAttribute("time");
            int timee = (int)time;
            int goodsId = (int)request.getAttribute("goodsId");
            userWatchLogService.userWatchLog(timee,userid, goodsId);
            request.removeAttribute("time");
        }
        List<MallIndexCategoryVO> categories = MallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            return "error/error_5xx";
        }
        List<MallIndexCarouselVO> carousels = MallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<MallIndexConfigGoodsVO> hotGoodses = MallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<MallIndexConfigGoodsVO> newGoodses = MallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<MallIndexConfigGoodsVO> recommendGoodses = MallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);
        request.setAttribute("carousels", carousels);
        request.setAttribute("hotGoodses", hotGoodses);
        request.setAttribute("newGoodses", newGoodses);
        request.setAttribute("recommendGoodses", recommendGoodses);
        List<MallGoods> recommendations =mallGoodsService.getStreamRecommendations(userid);
        request.setAttribute("recommendations", recommendations);
        List<MallGoods> getOnlineRec =mallGoodsService.getOnlineRec(userid);
        request.setAttribute("getOnlineRec", getOnlineRec);
        return "mall/index";
    }
}
