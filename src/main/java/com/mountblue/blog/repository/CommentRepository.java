package com.mountblue.blog.repository;

import com.mountblue.blog.entitites.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Custom query methods can be added here
}
