package com.example.project.controllers;

import com.example.project.models.entities.Topic;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TopicController {
    private final TopicService topicService;
    private final UserService userService;

    public TopicController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping("/topics/add")
    public String getAddPage() {
        return "topics/add";
    }

    @PostMapping("/topics/add")
    public String addTopic(@ModelAttribute(name = "addTopic") String name,
                           Model model) {
        try {
            topicService.add(userService.getCurrentLoggedIn().getUsername(), name);
            return "redirect:/topics/1";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "topics/add";
        }
    }

    @GetMapping("/topics/{id}/edit")
    public String getEditPage(@PathVariable int id,
                              Model model) {
        model.addAttribute("topic", topicService.getById(id));
        return "topics/edit";
    }


    @PostMapping("/topics/{id}/edit")
    public String editTopic(@ModelAttribute(name = "editTopic") String name,
                            @PathVariable int id,
                            Model model) {
        try {
            topicService.edit(id, name);
            return "redirect:/topics/1";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "topics/edit";
        }
    }

    @GetMapping("/topics/{id}/delete")
    public String deleteTopic(@PathVariable int id,
                              Model model) {
        topicService.remove(id);
        return "redirect:/topics/1";
    }

    @GetMapping("/topics/")
    public String redirectToCorrectTopicsPage() {
        String url = "/topics/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/user/{id}/topics/")
    public String redirectToCorrectUserTopicsPage(@PathVariable String id) {
        String url = "/user/" + id + "/topics/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/topics/{page}")
    public String getByPage(@PathVariable int page,
                            Model model) {
        List<Topic> topics = topicService.getPage(page - 1, 10);
        model.addAttribute("topics", topics);
        return "index";
    }

    @GetMapping("/user/{username}/topics/{page}")
    public String getByPageAndUsername(@PathVariable int page,
                                       @PathVariable String username,
                                       Model model) {
        List<Topic> topics = topicService.getPageByUsername(page - 1, 10, username);
        model.addAttribute("topics", topics);
        return "user/topics";
    }

    @GetMapping("/topics/search")
    public String searchTopicsByName(@RequestParam(name = "name") String name,
                                     Model model) {
        return "index";
    }

    @GetMapping("/user/{id}/topics/search")
    public String searchUserTopicsByName(@PathVariable String id,
                                         @RequestParam(name = "name") String name,
                                         Model model) {
        return "user/topics";
    }
}
