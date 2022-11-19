package com.example.project.controllers;

import com.example.project.dto.auth.RegisterDto;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogin(@RequestParam(name = "error", required = false) String error, Model model) {
        if (Boolean.parseBoolean(error))
            model.addAttribute("error", "Try another username or password");
        return "login";
    }

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute(name = "register") RegisterDto registerDto, Model model) {
        try {
            userService.register(registerDto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
        return "redirect:/login";
    }
}
