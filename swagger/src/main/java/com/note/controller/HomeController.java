package com.note.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping(value = "/swagger")
    public String index() {
        return "redirect:swagger-ui.html";
    }

    @RequestMapping(value = "/testzuul")
    @ResponseBody
    public String testzuul() {
        return "hello zuul";
    }

}