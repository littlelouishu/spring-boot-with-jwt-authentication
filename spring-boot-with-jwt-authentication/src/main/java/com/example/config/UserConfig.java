package com.example.config;

import com.example.security.user.InMemoryUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {
    
    @Bean
    @DependsOn("passwordEncoder")
    public InMemoryUserRepository inMemoryUserRepository(PasswordEncoder passwordEncoder) {
        return new InMemoryUserRepository(passwordEncoder);
    }
}