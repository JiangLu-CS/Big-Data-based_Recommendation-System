package lu.my.mall.controller.admin;

import lu.my.mall.controller.admin.aspect.Action;
import lu.my.mall.service.ShopmanUserService;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.Result;
import lu.my.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class ShopmanController {
    @Resource
    private ShopmanUserService shopmanUserService;

    @Action(value = "查看销售员")
    @RequestMapping(value = "/shopman/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(shopmanUserService.findShopmanList(pageUtil));
    }

    @GetMapping("/shopman")
    public String shopmanList(HttpServletRequest request) {
        request.setAttribute("path", "shopman");
        return "admin/shopman";
    }
}
