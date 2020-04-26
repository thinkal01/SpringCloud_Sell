package com.note.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimeZone;

@RestController
@SpringCloudApplication
/**
 * SpringBoot主启动类标注了@SpringBootApplication注解，该注解引入了@ComponentScan注解
 * 默认的包扫描规则是，自动扫描主启动类所在包及其子包
 *
 * 但是在多模块项目开发中，有时需要将公共模块的一个组件加入IOC容器，但其所在包又不在默认扫描范围内
 *
 * 解决办法两个：
 * 方法1：将公共模块中的组件放在默认扫描的包下（包名一样）
 * 方法2：使用@ComponentScan额外指定待扫描的包，但是不能用在主启动类上，因为这样会覆盖掉默认的包扫描规则，
 * 可以在其他标注了@Configuration的地方配置@ComponentScan(basePackages = { "xxx.yyy"})进行额外指定。
 * SpringBoot版本：2.1.4.RELEASE
 */
// @ComponentScan(basePackages = {"com.note.client.i18n.manage"})
@ComponentScan(basePackages = {"com.note.client.common"})
@Configuration
public class ClientApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(ClientApplication.class, args);
    }

}


