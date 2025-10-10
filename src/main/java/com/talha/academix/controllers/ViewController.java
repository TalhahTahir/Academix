package com.talha.academix.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves Thymeleaf views (server-rendered pages).
 */
@Controller
public class ViewController {

    @GetMapping("/enroll")
    public String enrollPage() {
        // Resolves to src/main/resources/templates/enroll.html
        return "enroll";
    }

    // (Optional) Simple home redirect
    @GetMapping("/")
    public String root() {
        return "enroll";
    }
}

