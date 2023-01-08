package com.example.project.controllers;

import com.example.project.dto.page.PageDto;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class TopicController {
    private final TopicService topicService;
    private final UserService userService;
    private static final String ERROR_URL = "/error";
    private static final String REDIRECT = "redirect:";

    public TopicController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping("/topics/choose")
    public String getChoosePage(@RequestParam(name = "query", required = false) String name,
                                Model model) {
        if (name != null && !name.equals("")) {
            List<Topic> topics = topicService.getByNameContains(name);
            model.addAttribute("topics", topics);
        }

        return "user/chooseTopic";
    }

    @PostMapping("/topics/choose")
    public String choose(@RequestParam(name = "topicName", required = false) String name) {
        Topic topic = topicService.getByName(name);
        String url = "/topic/" + topic.getId() + "/questions/add";

        return REDIRECT + url;
    }

    @GetMapping("/topics/add")
    public String getAddPage() {
        return "topics/add";
    }

    @PostMapping("/topics/add")
    public String add(@RequestParam(name = "name") String name,
                      Model model) {
        try {
            topicService.add(userService.getCurrentLoggedIn(), name);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "topics/add";
        }

        return REDIRECT + getUrlToUserPage(userService.getCurrentLoggedIn());
    }

    @GetMapping("/topics/edit/{id}")
    public String getEditPage(@PathVariable int id,
                              Model model) {
        try {
            model.addAttribute("topic", topicService.getById(id));
            return "topics/edit";
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }
    }

    @PostMapping("/topics/edit/{id}")
    public String edit(@RequestParam(name = "name") String name,
                       @PathVariable int id,
                       Model model) {
        try {
            topicService.edit(id, name);
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("topic", topicService.getById(id));
            model.addAttribute("error", ex.getMessage());
            return "topics/edit";
        }

        return REDIRECT + getUrlToUserPage(userService.getCurrentLoggedIn());
    }

    @GetMapping("/topics/delete/{id}")
    public String getDeletePage(@PathVariable int id,
                                @RequestParam(name = "query", required = false) String name,
                                Model model) {
        if (name != null && !name.equals("")) {
            List<Topic> topics = topicService.getByNameContains(name);
            model.addAttribute("topics", topics);
        }

        try {
            model.addAttribute("topic", topicService.getById(id));
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        return "topics/delete";
    }

    @PostMapping("/topics/delete/{id}")
    public String delete(@PathVariable int id,
                         @RequestParam(name = "topicName") String transferTopicName) {
        try {
            topicService.remove(id, transferTopicName);
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        return REDIRECT + getUrlToUserPage(userService.getCurrentLoggedIn());
    }

    @GetMapping("/admin/topics")
    public String redirectToAdminPage() {
        String url = "/admin/topics/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/topics")
    public String redirectToPage() {
        String url = "/topics/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/user/topics")
    public String redirectToUserPage() {
        String url = "/user/topics/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/admin/topics/{page}")
    public String getForAdmin(@PathVariable(name = "page") Integer page,
                              Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Topic> topics = topicService.getPage(page, 10);
        model.addAttribute("topics", topics.getElements());
        model.addAttribute("currentPage", topics.getCurrentPage());
        model.addAttribute("totalPages", topics.getTotalPages());
        return "admin/topics";
    }

    @GetMapping("/admin/topics/search")
    public String getByNameForAdmin(@RequestParam(name = "page", required = false) Integer page,
                                    @RequestParam(name = "query") String name,
                                    Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Topic> topics = topicService.getPageByName(page, 10, name);

        model.addAttribute("topics", topics.getElements());
        model.addAttribute("currentPage", topics.getCurrentPage());
        model.addAttribute("totalPages", topics.getTotalPages());
        model.addAttribute("name", name);
        model.addAttribute("isSearch", true);
        return "admin/topics";
    }

    @GetMapping("/topics/{page}")
    public String get(@PathVariable(name = "page") Integer page,
                      Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Topic> topics = topicService.getPage(page, 10);

        model.addAttribute("topics", topics.getElements());
        model.addAttribute("currentPage", topics.getCurrentPage());
        model.addAttribute("totalPages", topics.getTotalPages());
        return "main/topics";
    }

    @GetMapping("/topics/search")
    public String getByName(@RequestParam(name = "page", required = false) Integer page,
                            @RequestParam(name = "query") String name,
                            Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Topic> topics = topicService.getPageByName(page, 10, name);

        model.addAttribute("topics", topics.getElements());
        model.addAttribute("currentPage", topics.getCurrentPage());
        model.addAttribute("totalPages", topics.getTotalPages());
        model.addAttribute("name", name);
        model.addAttribute("isSearch", true);
        return "main/topics";
    }

    @GetMapping("/user/topics/{page}")
    public String getForUser(@PathVariable(name = "page") Integer page,
                             Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Topic> topics = topicService.getPageByUser(page, 10,
                userService.getCurrentLoggedIn());

        model.addAttribute("topics", topics.getElements());
        model.addAttribute("currentPage", topics.getCurrentPage());
        model.addAttribute("totalPages", topics.getTotalPages());

        return "user/topics";
    }

    @GetMapping("/user/topics/search")
    public String getByUsernameForUser(@RequestParam(name = "page", required = false) Integer page,
                                       @RequestParam(name = "query") String name,
                                       Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Topic> topics = topicService.getPageByNameAndUser(page, 10,
                userService.getCurrentLoggedIn(), name);

        model.addAttribute("topics", topics.getElements());
        model.addAttribute("currentPage", topics.getCurrentPage());
        model.addAttribute("totalPages", topics.getTotalPages());
        model.addAttribute("name", name);
        model.addAttribute("isSearch", true);

        return "user/topics";
    }

    private String getUrlToUserPage(User user) {
        if (user.getRole().equals(Role.USER)) {
            return "/user/topics";
        } else if (user.getRole().equals(Role.ADMIN)) {
            return "/admin/topics";
        }
        return null;
    }
}
