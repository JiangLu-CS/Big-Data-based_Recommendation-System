package lu.my.mall;

import lu.my.mall.entity.logEntity;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("lu.my.mall.dao")
@SpringBootApplication
public class LuMallApplication {
    logEntity logEntity = new logEntity();
    public static void main(String[] args) {
        SpringApplication.run(lu.my.mall.LuMallApplication.class, args);
    }
}

