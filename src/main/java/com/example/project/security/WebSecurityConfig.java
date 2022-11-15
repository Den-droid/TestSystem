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
    private final static String[] USER_PATTERNS = new String[]{
            "/user/**", "/topics/**"
    };

    private final static String[] ADMIN_PATTERNS = new String[]{
            "/admin/**"
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
                .antMatchers("/").permitAll()
                .antMatchers("/register").anonymous()
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
