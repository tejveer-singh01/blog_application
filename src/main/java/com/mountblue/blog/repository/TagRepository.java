package com.mountblue.blog.repository;

import com.mountblue.blog.entitites.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    // Custom query methods can be added here
}
