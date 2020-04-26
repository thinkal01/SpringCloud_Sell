package com.note.client.i18n.exception;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class I18NController {
    @RequestMapping("/demo")
    public String demo(String msg) {
        // 模拟带占位符的A异常
        if ("a".equals(msg)) {
            throw new AppException("010101", "haha");
        }
        // 模拟不带占位符的B异常
        if ("b".equals(msg)) {
            throw new AppException("010102");
        }
        // 模拟其他异常
        if ("c".equals(msg)) {
            throw new RuntimeException();
        }
        return "";
    }
}
