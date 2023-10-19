package com.mountblue.blog.repository;

import com.mountblue.blog.entitites.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Custom query methods can be added here
    Post saveAndFlush(Post post);
}
