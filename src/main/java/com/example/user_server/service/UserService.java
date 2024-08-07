package com.example.user_server.service;

import com.example.user_server.dto.UserInfo;
import com.example.user_server.dto.UserRequest;
import com.example.user_server.entity.User;
import com.example.user_server.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    @Transactional
    public UserInfo createUser(UserRequest userRequest) {
        // Password hash
        String hashedPassword = passwordEncoder.encode(userRequest.getPlainPassword());

        // unique 한 userName 검증
        if (userRepository.findByUsername(userRequest.getUsername()) != null) {
            throw new RuntimeException("Username duplicated");
        }

        User user = new User(userRequest.getUsername(), userRequest.getEmail(), hashedPassword);
        User savedUser = userRepository.save(user);

        return new UserInfo(savedUser);
    }

    public UserInfo getUser(int userId) {
        User user = userRepository.findById(userId).orElse(null);

        if(user == null) {
            return null;
        }

        return new UserInfo(user);
    }

    public UserInfo getUserByName(String name) {
        User user = userRepository.findByUsername(name);

        if(user == null) {
            return null;
        }

        return new UserInfo(user);
    }

    // 로그인
    public UserInfo signIn(UserRequest request) {
        User user = null;

        if(request.getUsername() != null) {
            user = userRepository.findByUsername(request.getUsername());
        }

        if(user == null) {
            return null;
        }

        boolean isPasswordMatch = passwordEncoder.matches(request.getPlainPassword(), user.getPassword());

        if (!isPasswordMatch) {
            return null;
        }

        return new UserInfo(user);
    }
}
