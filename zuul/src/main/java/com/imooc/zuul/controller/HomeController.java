package com.imooc.zuul.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 编写一个控制类，测试forward功能
 * zuul 内部提供对外服务示例
 */
@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/hello")
    public String hello(String name) {
        return "hi," + name + ",this is zuul api! ";
    }

    @GetMapping("/common")
    @ResponseBody
    public String common() {
        return "未找到对应的服务,请检查您的请求参数或者服务注册配置!";
    }

}