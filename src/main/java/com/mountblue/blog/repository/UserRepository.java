package com.mountblue.blog.repository;

import com.mountblue.blog.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here

    User findByName(String username);
}
