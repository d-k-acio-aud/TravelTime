package com.example.travel_time.service;


import com.example.travel_time.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.travel_time.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Хешируем пароль
        User savedUser = userRepository.save(user);
        System.out.println("Сохраненный пользователь: " + savedUser);
        return savedUser;
    }

    public User updateUser(Long id, User newUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setEmail(newUser.getEmail());
                    if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

