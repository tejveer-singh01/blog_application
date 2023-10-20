package com.mountblue.blog.service;

import com.mountblue.blog.entitites.Post;
import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        Post post = postRepository.findById(postId).orElse(null);

        if(post != null) {
            post.getTags().clear();
            postRepository.save(post);
            postRepository.deleteById(postId);
        }

        // check if the post exists
//        if(postRepository.existsById(postId)){
//            postRepository.deleteById(postId);
//        }
        // Handle the case where the post does not exist, if needed
    }

    public Post savePostWithTags(Post post) {
        // Save the post with associated tags and return the saved post
        return postRepository.saveAndFlush(post);
    }


    // Sorting

    public List<Post> getAllPostsSortedByDateDesc() {
        return postRepository.findAll(Sort.by(Sort.Order.desc("publishedAt")));
    }

    public List<Post> getAllPostsSortedByDateAsc() {
        return postRepository.findAll(Sort.by(Sort.Order.asc("publishedAt")));
    }

    public List<Post> getAllPostsSortedByTitleAsc() {
        return postRepository.findAll(Sort.by(Sort.Order.asc("title")));
    }

    public List<Post> getAllPostsSortedByTitleDesc() {
        return postRepository.findAll(Sort.by(Sort.Order.desc("title")));
    }

    // PostService.java

    public List<Post> getAllPostsSortedByAuthorAsc() {
        return postRepository.findAll(Sort.by(Sort.Order.asc("author.name")));
    }

    public List<Post> getAllPostsSortedByAuthorDesc() {
        return postRepository.findAll(Sort.by(Sort.Order.desc("author.name")));
    }


    // searching

    public List<Post> searchPostsByKeyword(String keywords) {
//        return postRepository.searchByKeyword(keywords);

        // Split input keywords into individual words
        String[] keywordArray = keywords.split("\\s+");
        Set<String> keywordSet = new HashSet<>(Arrays.asList(keywordArray));

        // Perform search for each keyword and combine the results
        List<Post> searchResults = keywordSet.stream()
                .map(keyword -> postRepository.searchByKeyword(keyword))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        return searchResults;
    }


    // filter

    public List<User> getAllAuthors() {
        return postRepository.getAllAuthors();
    }

    public List<Post> getPostsByAuthorId(Long authorId) {
        // Assuming you have a repository method to fetch posts by author ID
        return postRepository.getPostsByAuthorId(authorId);
    }

    public List<Date> getAllPublishDates() {
        List<Post> posts = postRepository.findAll(); // Retrieve all posts from the repository
        return posts.stream()
                .map(Post::getPublishedAt)
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
    }



//    public List<Post> filterPosts(List<Long> authorIds, List<Long> tagIds, List<Date> publishDates) {
//        List<Post> filteredPosts = new ArrayList<>();
//
//        for (Post post : getAllPosts()) {
//            boolean matchesCriteria = true;
//
//            // Check author filter
//            if (authorIds != null && !authorIds.isEmpty()) {
//                if (!authorIds.contains(post.getAuthor().getId())) {
//                    matchesCriteria = false;
//                }
//            }
//
//            // Check tag filter
//            if (matchesCriteria && tagIds != null && !tagIds.isEmpty()) {
//                boolean containsAllTags = post.getTags().stream().allMatch(tag -> tagIds.contains(tag.getId()));
//                if (!containsAllTags) {
//                    matchesCriteria = false;
//                }
//            }
//
//            // Check publish date filter
//            if (matchesCriteria && publishDates != null && !publishDates.isEmpty()) {
//                boolean containsAnyDate = publishDates.stream().anyMatch(date -> date.equals(post.getPublishedAt()));
//                if (!containsAnyDate) {
//                    matchesCriteria = false;
//                }
//            }
//
//            if (matchesCriteria) {
//                filteredPosts.add(post);
//            }
//        }
//
//        return filteredPosts;
//    }


    public List<Post> filterPosts(List<Long> authorIds, List<Long> tagIds, List<Date> publishDates) {
        List<Post> filteredPosts = new ArrayList<>();

        for (Post post : getAllPosts()) {
            boolean matchesCriteria = false;

            // Check author filter
            if (authorIds != null && !authorIds.isEmpty()) {
                if (authorIds.contains(post.getAuthor().getId())) {
                    matchesCriteria = true;
                }
            }

            // Check tag filter
            if (!matchesCriteria && tagIds != null && !tagIds.isEmpty()) {
                boolean containsAnyTag = post.getTags().stream().anyMatch(tag -> tagIds.contains(tag.getId()));
                if (containsAnyTag) {
                    matchesCriteria = true;
                }
            }

            // Check publish date filter
            if (!matchesCriteria && publishDates != null && !publishDates.isEmpty()) {
                boolean containsAnyDate = publishDates.stream().anyMatch(date -> date.equals(post.getPublishedAt()));
                if (containsAnyDate) {
                    matchesCriteria = true;
                }
            }

            if (matchesCriteria) {
                filteredPosts.add(post);
            }
        }

        return filteredPosts;
    }



}
