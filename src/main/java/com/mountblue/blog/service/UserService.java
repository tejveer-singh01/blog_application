package com.mountblue.blog.service;

import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User author) {
        author.setPassword(bCryptPasswordEncoder.encode(author.getPassword()));
        userRepository.save(author);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    // Implement service methods for user-related operations


}
