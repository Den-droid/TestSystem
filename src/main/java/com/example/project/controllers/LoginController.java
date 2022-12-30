package com.example.project.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    public LoginController() {
    }

    @GetMapping("/login")
    public String getLogin(@RequestParam(name = "error", required = false) String error,
                           Model model) {
        if (Boolean.parseBoolean(error)) {
            model.addAttribute("error", "Try another username or password");
        }
        return "login";
    }
}
