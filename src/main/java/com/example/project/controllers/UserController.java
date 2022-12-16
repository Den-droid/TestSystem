package com.example.project.controllers;

import com.example.project.dto.user.EditUserDto;
import com.example.project.models.entities.User;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/profile")
    public String getProfilePage(Model model) {
        User user = userService.getCurrentLoggedIn();
        model.addAttribute("user", userService.getProfile(user));
        return "user/profile";
    }

    @PostMapping("/user/profile")
    public String changeUser(@ModelAttribute(name = "editProfile") EditUserDto dto,
                             Model model) {
        User user = userService.getCurrentLoggedIn();
        if (dto.getAction().equals("edit")) {
            try {
                userService.edit(user, dto);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("error", ex.getMessage());
                model.addAttribute("user", userService.getProfile(user));
                return "user/profile";
            }
        } else if (dto.getAction().equals("delete")) {
            userService.delete(user);
        }

        return "redirect:/logout";
    }
}
