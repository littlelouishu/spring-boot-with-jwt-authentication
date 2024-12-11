package com.example.security.user;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;

public class InMemoryUserRepository {
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong();

    private final PasswordEncoder passwordEncoder;

    public InMemoryUserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        save(
            User.builder()
                .username("admin")
                .password("password")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .enabled(true)
                .build());
        save(
            User.builder()
                .username("user")
                .password("password")
                .email("user@example.com")
                .role(Role.USER)
                .enabled(true)
                .build());
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
}
