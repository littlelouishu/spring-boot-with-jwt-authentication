package com.example.security;

import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.security.user.InMemoryUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@DependsOn("inMemoryUserRepository")
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {
    
    private final InMemoryUserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    String.format("User with username - %s, not found", username)
                ));
    }
}