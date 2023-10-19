package com.mountblue.blog.repository;

import com.mountblue.blog.entitites.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    // Custom query methods can be added here

    Tag findByName(String name);
}
