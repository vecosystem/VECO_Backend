package com.example.Veco.global.auth.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/test")
@Controller
public class TestController {

    @RequestMapping("/login")
    public String hello() {
        return "forward:/login-test.html";
    }
}
