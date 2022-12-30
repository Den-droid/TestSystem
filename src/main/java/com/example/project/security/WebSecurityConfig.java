package com.example.project.security;

import com.example.project.models.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private static final String[] USER_PATTERNS = new String[]{
            "/user/**", "/test/**", "/topics/add", "/topics/edit/{id}",
            "/topic/{id}/questions/add", "/questions/edit/{questionId}",
            "/tests/generate"
    };

    private static final String[] ADMIN_PATTERNS = new String[]{
            "/admin/**"
    };

    private static final String[] SHARED_PATTERNS = new String[]{
            "/topics/delete/*", "/questions/delete/{questionId}", "/profile"
    };

    private static final String[] PERMITTED_PATTERNS = new String[]{
            "/", "/register", "/static/**", "/uploads/**",
            "/topics", "/topics/*"
    };

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain getChain(HttpSecurity security) throws Exception {
        security.authorizeRequests()
                .antMatchers(USER_PATTERNS).hasAuthority(Role.USER.name())
                .antMatchers(ADMIN_PATTERNS).hasAuthority(Role.ADMIN.name())
                .antMatchers(SHARED_PATTERNS).hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                .antMatchers(PERMITTED_PATTERNS).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .httpBasic()
                .disable()
                .csrf()
                .disable();
        return security.build();
    }
}
