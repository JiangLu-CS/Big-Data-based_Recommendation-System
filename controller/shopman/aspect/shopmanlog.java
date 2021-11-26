package lu.my.mall.controller.shopman.aspect;

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
public class shopmanlog {

    @Resource
    private logService logService;


    @Pointcut("@annotation(lu.my.mall.controller.shopman.aspect.shopmanAction) ")
    public void log(){
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        shopmanAction action = method.getAnnotation(shopmanAction.class);
        String value = action.value();
        String URL = "lumall";
        String IP = "120.238.248.159";
        String function = value;
        String user = "shopman";
        String param = Arrays.toString(joinPoint.getArgs());;
        Date time = new Date();
        logService.shopmanInsertLog(URL,  IP,  param,  time,  user,  function);
    }
}
