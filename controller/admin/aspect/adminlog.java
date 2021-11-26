package lu.my.mall.controller.admin.aspect;

import lu.my.mall.service.logService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
public class adminlog {
    @Resource
    private logService logService;
    @Pointcut("@annotation(lu.my.mall.controller.admin.aspect.Action) ")
    public void log(){
    }
    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Action action = method.getAnnotation(Action.class);
        String value = action.value();
        String URL = "lumall";
        String IP = "120.238.248.159";
        String function = value;
        String user = "admin";
        String param = Arrays.toString(joinPoint.getArgs());
        Date time = new Date();
        logService.insertLog(URL,  IP,  param,  time,  user,  function);
    }
}
