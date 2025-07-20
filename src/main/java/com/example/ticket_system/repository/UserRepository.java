package com.example.ticket_system.repository;

import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.User;
import org.springframework.stereotype.Repository;


import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;

    public UserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Initialize mock users on application startup
    @PostConstruct
    public void init() {
        // Mock USER
        User user1 = new User("user1-id", "user1", passwordEncoder.encode("pass123"), Role.USER);
        User user2 = new User("user2-id", "john", passwordEncoder.encode("pass123"), Role.USER);
        users.put(user1.getUsername(), user1);
        users.put(user2.getUsername(), user2);

        // Mock AGENT
        User agent1 = new User("agent1-id", "agent1", passwordEncoder.encode("agentpass"), Role.AGENT);
        users.put(agent1.getUsername(), agent1);

        User agent2 = new User("agent2-id", "agent2", passwordEncoder.encode("agentpass"), Role.AGENT);
        users.put(agent2.getUsername(), agent2);

        System.out.println("Mock users initialized.");
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByUserId(String userId) {
        return users.values().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    public List<User> findByRole(Role role) {
        return users.values().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }
}
