package com.mountblue.blog.service;

import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User author) {
        userRepository.save(author);
    }

    // Implement service methods for user-related operations
}
