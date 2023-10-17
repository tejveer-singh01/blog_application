package com.mountblue.blog.service;

import com.mountblue.blog.entitites.Post;
import com.mountblue.blog.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Transactional
@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

//    public void SavePost(String title, String excerpt, String content,  String author){
//        Post p = new Post();
//        p.setTitle(title);
//        p.setExcerpt(excerpt);
//        p.setContent(content);
//        postRepository.save(p);
//    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public Post getPostById(Long postId){
        return postRepository.findById(postId).orElse(null);
    }

    public Post createPost(Post post){
        // Additional logic for setting timestamps, author, etc., if needed
        return postRepository.save(post);
    }

    public Post updatePost(Long postId, Post updatedPost){

        // check if post exists
        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Post not found with id: " + postId);
//            return null; // Post not found, return null or throw an exception
        }

        // update the existing post with the new data
        updatedPost.setId(postId);

        // Additional logic for setting timestamps, author, etc., if need
        return postRepository.save(updatedPost);
    }

    public void deletePost(Long postId){

        postRepository.deleteById(postId);

        // check if the post exists
//        if(postRepository.existsById(postId)){
//            postRepository.deleteById(postId);
//        }
        // Handle the case where the post does not exist, if needed
    }

}
