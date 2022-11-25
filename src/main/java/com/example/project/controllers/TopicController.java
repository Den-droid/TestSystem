package com.example.project.controllers;

import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;

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
    public String addTopic(@RequestParam(name = "name") String name,
                           Model model) {
        try {
            String username = userService.getCurrentLoggedIn().getUsername();
            topicService.add(username, name);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "topics/add";
        }
        String url = getRedirectUrlToUserTopicPage();
        return "redirect:" + url;
    }

    @GetMapping("/topics/edit/{id}")
    public String getEditPage(@PathVariable int id,
                              Model model) {
        try {
            model.addAttribute("topic", topicService.getById(id));
            return "topics/edit";
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }
    }

    @PostMapping("/topics/edit/{id}")
    public String editTopic(@RequestParam(name = "name") String name,
                            @PathVariable int id,
                            Model model) {
        try {
            topicService.edit(id, name);
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "topics/edit";
        }
        String url = getRedirectUrlToUserTopicPage();
        return "redirect:" + url;
    }

    @GetMapping("/topics/delete/{id}")
    public String getDeleteTopic(@PathVariable int id) {
        //Get all topics
        return "topics/delete";
    }

    @PostMapping("/topics/delete/{id}")
    public String deleteTopic(@PathVariable int id,
                              @RequestParam(name = "topicName") String transferTopicName) {
        try {
            topicService.remove(id, transferTopicName);
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }
        String url = getRedirectUrlToUserTopicPage();
        return "redirect:" + url;
    }

    @GetMapping("/admin/topics")
    public String redirectToCorrectAdminTopicsPage() {
        String url = "admin/topics/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/admin/topics/search")
    public String redirectToSearchTopicsByNameForAdmin(@RequestParam(name = "name", required = false) String name) {
        String url = "admin/topics/search/" + 1 + "?name=" + name;
        return "redirect:" + url;
    }

    @GetMapping("/topics")
    public String redirectToCorrectTopicsPage() {
        String url = "/topics/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/topics/search")
    public String redirectToSearchTopicsByName(@RequestParam(name = "name", required = false) String name) {
        String url = "/topics/search/" + 1 + "?name=" + name;
        return "redirect:" + url;
    }

    @GetMapping("/user/{username}/topics")
    public String redirectToCorrectUserTopicsPage(@PathVariable String username) {
        String url = "/user/" + username + "/topics/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/user/{username}/topics/search")
    public String redirectToSearchUserTopicsByName(@PathVariable String username,
                                                   @RequestParam(name = "name", required = false) String name) {
        String url = "/user/" + username + "/topics/search/" + 1 + "?name=" + name;
        return "redirect:" + url;
    }

    @GetMapping("/admin/topics/{page}")
    public String getByPageForAdmin(@PathVariable int page,
                                    Model model) {
        Page<Topic> topics = topicService.getPage(page, 10);
        model.addAttribute("topics", topics.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", topics.getTotalPages());
        return "admin/topics";
    }

    @GetMapping("/admin/topics/search/{page}")
    public String searchTopicsByNameForAdmin(@RequestParam(name = "name", required = false) String name,
                                             @PathVariable int page,
                                             Model model) {
        Page<Topic> topics = topicService.getSearchPageByName(page, 10, name);
        model.addAttribute("topics", topics.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", topics.getTotalPages());
        return "admin/topics";
    }

    @GetMapping("/topics/{page}")
    public String getByPage(@PathVariable int page,
                            Model model) {
        Page<Topic> topics = topicService.getPage(page, 10);
        model.addAttribute("topics", topics.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", topics.getTotalPages());
        return "main/topics";
    }

    @GetMapping("/topics/search/{page}")
    public String searchTopicsByName(@RequestParam(name = "name", required = false) String name,
                                     @PathVariable int page,
                                     Model model) {
        Page<Topic> topics = topicService.getSearchPageByName(page, 10, name);
        model.addAttribute("topics", topics.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", topics.getTotalPages());
        return "main/topics";
    }

    @GetMapping("/user/{username}/topics/{page}")
    public String getByPageAndUsername(@PathVariable int page,
                                       @PathVariable String username,
                                       Model model) {
        try {
            Page<Topic> topics = topicService.getPageByUsername(page, 10, username);
            model.addAttribute("topics", topics.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", topics.getTotalPages());
            return "user/topics";
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }
    }

    @GetMapping("/user/{username}/topics/search/{page}")
    public String searchUserTopicsByName(@PathVariable String username,
                                         @PathVariable int page,
                                         @RequestParam(name = "name", required = false) String name,
                                         Model model) {
        try {
            Page<Topic> topics = topicService.getSearchPageByNameAndUsername(page, 10, username, name);
            model.addAttribute("topics", topics.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", topics.getTotalPages());
            return "user/topics";
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }
    }

    private String getRedirectUrlToUserTopicPage() {
        User user = userService.getCurrentLoggedIn();
        switch (user.getRole()) {
            case USER:
                return "/user/" + user.getUsername() + "/topics";
            case ADMIN:
                return "/admin/topics";
        }
        return null;
    }
}
