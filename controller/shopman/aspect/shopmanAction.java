package lu.my.mall.controller.shopman.aspect;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface shopmanAction {
    String value() default "";

}