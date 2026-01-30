package com.talha.academix.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";   
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback() {
        return "oauth2-callback";
    }

    @GetMapping("/enroll")
    public String enrollPage() {
        return "enroll";
    }

}