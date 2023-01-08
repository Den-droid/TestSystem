package com.example.project.models.services.impl;

import com.example.project.dto.user.RegisterDto;
import com.example.project.dto.user.EditUserProfileDto;
import com.example.project.dto.user.UserProfileDto;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import com.example.project.models.mappers.EditUserMapper;
import com.example.project.models.mappers.RegisterMapper;
import com.example.project.models.mappers.UserProfileMapper;
import com.example.project.models.repositories.QuestionRepository;
import com.example.project.models.repositories.TestRepository;
import com.example.project.models.repositories.TopicRepository;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    public UserServiceImpl(UserRepository userRepository,
                           TopicRepository topicRepository,
                           QuestionRepository questionRepository,
                           TestRepository testRepository) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    @Override
    public void register(RegisterDto dto) {
        User user = RegisterMapper.map(dto);
        User check = userRepository.findByUsernameIgnoreCase(user.getUsername());
        if (check != null) {
            throw new IllegalArgumentException("There is already user with such username existed!!!");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new NoSuchElementException("There is no user with username "
                    + username + "!!!");
        }
        return user;
    }

    @Override
    public UserProfileDto getProfile(User user) {
        return UserProfileMapper.map(user);
    }

    @Override
    public void edit(User user, EditUserProfileDto dto) {
        User repoUser = userRepository.findByUsernameIgnoreCase(dto.getUsername());
        if (repoUser != null && !repoUser.equals(user)) {
            throw new IllegalArgumentException("There is already user with such username!!!");
        }

        EditUserMapper.map(user, dto);
        if (dto.getChangePassword() != null) {
            user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        }

        userRepository.save(user);
        changeAuthentication(user.getUsername(), user.getPassword());
    }

    @Override
    public void delete(User user) {
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);

        Random random = new Random();
        User admin = admins.get(random.nextInt(admins.size()));

        user.getTopics().forEach(x -> x.setUser(admin));
        topicRepository.saveAll(user.getTopics());

        user.getQuestions().forEach(x -> x.setUser(admin));
        questionRepository.saveAll(user.getQuestions());

        user.getTestsCreated().forEach(x -> x.setUserCreated(admin));
        testRepository.saveAll(user.getTestsCreated());

        user.getAssignedTests().forEach(x -> x.getUsersAssigned().remove(user));
        testRepository.saveAll(user.getAssignedTests());

        userRepository.delete(user);
        deleteAuthentication();
    }

    @Override
    public User getCurrentLoggedIn() {
        Object userObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userObject instanceof UserDetails) {
            return userRepository.findByUsernameIgnoreCase(
                    ((UserDetails) userObject).getUsername());
        }
        return null;
    }

    @Override
    public List<User> getByUsernameContainsAndUsernamesNotInAndRole(String usernamePart,
                                                                    List<String> usernamesNotIn,
                                                                    Role role) {
        List<User> users;
        if (usernamesNotIn == null || usernamesNotIn.isEmpty()) {
            users = userRepository.findAllByUsernameContainsIgnoreCase(usernamePart);
        } else {
            users = userRepository.findAllByUsernameContainsIgnoreCaseAndUsernameNotIn
                    (usernamePart, usernamesNotIn);
        }
        return users.stream()
                .filter(x -> x.getRole().equals(Role.USER))
                .collect(Collectors.toList());
    }

    private void changeAuthentication(String newUsername, String newPassword) {
        Collection<SimpleGrantedAuthority> nowAuthorities =
                (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getAuthorities();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(newUsername, newPassword, nowAuthorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void deleteAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}
