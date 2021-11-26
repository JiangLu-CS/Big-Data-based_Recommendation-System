package lu.my.mall.interceptor;

import lu.my.mall.service.AdminUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LogInterceptor implements HandlerInterceptor {
   // @Resource
    //logService logService;

    @Resource
    AdminUserService adminUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestServletPath = request.getServletPath();
        String URL = "lumall";
        String IP = "127.0.0.1";
        String function = "hihi";
        String user = "admin";
        String param = "hihi";
        Date time = new Date();
        //logService.insertLog(URL,  IP,  param,  time,  user,  function);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

}
