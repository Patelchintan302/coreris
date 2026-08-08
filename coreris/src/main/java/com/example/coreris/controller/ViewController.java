package com.example.coreris.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String RedirectToLogin(){
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String LoginPage(){
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(){
        return "dashboard";
    }
}
