package com.imooc.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@SpringCloudApplication
public class ClientApplication {
    @Value("${client}")
    private String client;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @GetMapping("/client/test")
    private String test(String phone) {
        return "hello " + client;
    }

    @GetMapping("/info")
    private String info() {
        return "hello " + client;
    }
}
