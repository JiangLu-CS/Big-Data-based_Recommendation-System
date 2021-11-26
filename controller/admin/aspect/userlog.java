package lu.my.mall.controller.admin.aspect;

import lu.my.mall.controller.vo.MallUserVO;
import lu.my.mall.service.logService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class userlog {

    @Resource
    private logService logService;


    @Pointcut("@annotation(lu.my.mall.controller.admin.aspect.userAction) ")
    public void log(){
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){


        // 记录下请求内容
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        userAction action = method.getAnnotation(userAction.class);
        String value = action.value();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();

        String URL = request.getRequestURL().toString();
        String IP = "120.238.248.159";
        String function = value;
        String user = "";
        if(session.getAttribute("MallUser") != null){
            MallUserVO mallUserVO = new MallUserVO();
            mallUserVO = (MallUserVO)session.getAttribute("MallUser");
            Integer id = Math.toIntExact(mallUserVO.getUserId());
            user = String.valueOf(id);
            if(user == ""){
                user = "0";
            }

        }
        String param = String.valueOf(joinPoint.getArgs()[0]);
        Date time = new Date();
        logService.userInsertLog(URL,  IP,  param,  time,  user,  function);
    }
}
