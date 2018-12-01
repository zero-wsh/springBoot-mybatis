package com.wsh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
// Swagger2访问 http://localhost:8888/swagger-ui.html
@SpringBootApplication
@MapperScan("com.wsh.zero.mapper")
@ComponentScan({"com.wsh.zero.controller", "com.wsh.zero.service"})
@EnableTransactionManagement//事务管理
@EnableSwagger2
public class SpringBootMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMybatisApplication.class, args);
    }
}