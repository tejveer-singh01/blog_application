package com.mountblue.blog.service;

import com.mountblue.blog.entitites.Post;
import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UserRepository userRepository;

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

    public Page<Post> getAllPostsPagedAndSorted(int page, int size, String sortField, String sortOrder) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(direction, sortField != null ? sortField : "defaultSortField");
        Pageable pageable = PageRequest.of(page, size, sort);

        return postRepository.findAll(pageable);
    }


//    public List<Post> getAllPostsSortedByDateDesc() {
//        return postRepository.findAll(Sort.by(Sort.Order.desc("publishedAt")));
//    }

    public Page<Post> getAllPostsSortedByDateDesc(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.desc("publishedAt"))));
    }

//    public List<Post> getAllPostsSortedByDateAsc() {
//        return postRepository.findAll(Sort.by(Sort.Order.asc("publishedAt")));
//    }

    public Page<Post> getAllPostsSortedByDateAsc(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("publishedAt"))));
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

//    public List<Post> searchPostsByKeyword(String keywords) {
//
//        String[] keywordArray = keywords.split("\\s+");
//        Set<String> keywordSet = new HashSet<>(Arrays.asList(keywordArray));
//
//        // Perform search for each keyword and combine the results
//        List<Post> searchResults = keywordSet.stream()
//                .map(keyword -> postRepository.searchByKeyword(keyword))
//                .flatMap(List::stream)
//                .distinct()
//                .collect(Collectors.toList());
//
//        return searchResults;
//    }


    public Page<Post> searchPostsByKeyword(String keywords, int page, int size) {
        String[] keywordArray = keywords.split("\\s+");
        Set<String> keywordSet = new HashSet<>(Arrays.asList(keywordArray));

        // Perform search for each keyword and combine the results
        List<Post> searchResults = keywordSet.stream()
                .map(keyword -> postRepository.searchByKeyword(keyword)) // Assuming this method is provided by your repository
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        // Paginate the search results
        int start = (int) page * size;
        int end = Math.min(start + size, searchResults.size());
        List<Post> paginatedResults = searchResults.subList(start, end);

        return new PageImpl<>(paginatedResults, PageRequest.of(page, size), searchResults.size());
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


//    public List<Post> filterPosts(List<Long> authorIds, List<Long> tagIds, List<Date> publishDates) {
//        List<Post> filteredPosts = new ArrayList<>();
//
//        for (Post post : getAllPosts()) {
//            boolean matchesCriteria = false;
//
//            // Check author filter
//            if (authorIds != null && !authorIds.isEmpty()) {
//                if (authorIds.contains(post.getAuthor().getId())) {
//                    matchesCriteria = true;
//                }
//            }
//
//            // Check tag filter
//            if (!matchesCriteria && tagIds != null && !tagIds.isEmpty()) {
//                boolean containsAnyTag = post.getTags().stream().anyMatch(tag -> tagIds.contains(tag.getId()));
//                if (containsAnyTag) {
//                    matchesCriteria = true;
//                }
//            }
//
//            // Check publish date filter
//            if (!matchesCriteria && publishDates != null && !publishDates.isEmpty()) {
//                boolean containsAnyDate = publishDates.stream().anyMatch(date -> date.equals(post.getPublishedAt()));
//                if (containsAnyDate) {
//                    matchesCriteria = true;
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

    public Page<Post> filterPosts(List<Long> authorIds, List<Long> tagIds, List<Date> publishDates, Pageable pageable) {
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

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredPosts.size());
        List<Post> content = filteredPosts.subList(start, end);

        return new PageImpl<>(content, pageable, filteredPosts.size());
    }

//    public Page<Post> filterPosts(List<Long> authorIds, List<Long> tagIds, List<Date> publishDates, int page, int size) {
//        // Get all posts from the repository
//        List<Post> allPosts = postRepository.findAll();
//
//        // Filter posts based on authorIds, tagIds, and publishDates
//        List<Post> filteredPosts = allPosts.stream()
//                .filter(post -> (authorIds == null || authorIds.isEmpty() || authorIds.contains(post.getAuthor().getId()))
//                        && (tagIds == null || tagIds.isEmpty() || post.getTags().stream().anyMatch(tag -> tagIds.contains(tag.getId())))
//                        && (publishDates == null || publishDates.isEmpty() || publishDates.contains(post.getPublishedAt())))
//                .collect(Collectors.toList());
//
//        // Paginate the filtered posts
//        int start = page * size;
//        int end = Math.min(start + size, filteredPosts.size());
//        List<Post> content = filteredPosts.subList(start, end);
//
//        return new PageImpl<>(content, PageRequest.of(page, size), filteredPosts.size());
//    }

    // pagination

    public Page<Post> getAllPostsPaged(Pageable pageable) {
        return postRepository.findAll(pageable);
    }


public List<Post> getMyBlogList() {
        User user = userRepository.findByName(SecurityContextHolder.getContext().getAuthentication().getName());
        return postRepository.getPostsByAuthorId(user.getId());
    }
}
