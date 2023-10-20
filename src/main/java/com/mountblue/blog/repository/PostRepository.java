package com.mountblue.blog.repository;

import com.mountblue.blog.entitites.Post;
import com.mountblue.blog.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Custom query methods can be added here
    Post saveAndFlush(Post post);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.author a " +
            "LEFT JOIN p.tags t " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchByKeyword(@Param("keyword") String keyword);


    @Query("SELECT DISTINCT p.author FROM Post p")
    List<User> getAllAuthors();

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId")
    List<Post> getPostsByAuthorId(@Param("authorId") Long authorId);
}
