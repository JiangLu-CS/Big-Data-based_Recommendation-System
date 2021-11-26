
package lu.my.mall.controller.mall;

import lu.my.mall.common.Constants;
import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.admin.aspect.userAction;
import lu.my.mall.controller.vo.MallUserVO;
import lu.my.mall.entity.MallUser;
import lu.my.mall.service.MallUserService;
import lu.my.mall.util.MD5Util;
import lu.my.mall.util.Result;
import lu.my.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PersonalController {

    @Resource
    private MallUserService MallUserService;

    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request,
                               HttpSession httpSession) {
        request.setAttribute("path", "personal");
        return "mall/personal";
    }


    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }

    @GetMapping({"/login", "login.html"})
    public String loginPage() {
        return "mall/login";
    }

    @GetMapping({"/register", "register.html"})
    public String registerPage() {
        return "mall/register";
    }

    @GetMapping("/personal/addresses")
    public String addressesPage() {
        return "mall/addresses";
    }

    @userAction("登录")
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName") String loginName,
                        @RequestParam("verifyCode") String verifyCode,
                        @RequestParam("password") String password,
                        HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult("失败");
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult("密码为空");
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult("验证码为空");
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult("失败");
        }
        String loginResult = MallUserService.login(loginName, MD5Util.MD5Encode(password, "UTF-8"), httpSession);
        if (ServiceResultEnum.SUCCESS.getResult().equals("成功")) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("失败");
    }

    @userAction(value = "注册")
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName") String loginName,
                           @RequestParam("verifyCode") String verifyCode,
                           @RequestParam("password") String password,
                           HttpSession httpSession) {
        if (StringUtils.isEmpty(loginName)) {
            return ResultGenerator.genFailResult("注册失败");
        }
        if (StringUtils.isEmpty(password)) {
            return ResultGenerator.genFailResult("密码为空");
        }
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.genFailResult("验证码为空");
        }
        String kaptchaCode = httpSession.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.toLowerCase().equals(kaptchaCode)) {
            return ResultGenerator.genFailResult("注册失败");
        }
        String registerResult = MallUserService.register(loginName, password);
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("失败");
    }

    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody MallUser mallUser, HttpSession httpSession) {
        MallUserVO mallUserTemp = MallUserService.updateUserInfo(mallUser,httpSession);
        if (mallUserTemp == null) {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        } else {
            Result result = ResultGenerator.genSuccessResult();
            return result;
        }
    }
}
