package com.mountblue.blog.controller;

import com.mountblue.blog.entitites.Comment;
import com.mountblue.blog.entitites.Post;
import com.mountblue.blog.entitites.Tag;
import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.service.CommentService;
import com.mountblue.blog.service.PostService;
import com.mountblue.blog.service.TagService;
import com.mountblue.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final TagService tagService;

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService, TagService tagService){
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.tagService = tagService;
    }


    @GetMapping("/posts")
    public String getAllPosts(Model model){
        List<Post> posts = postService.getAllPosts();
        System.out.println(posts);
        model.addAttribute("posts", posts);

        return "posts/list";
    }

    @GetMapping("/posts/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("post_comments",post.getComments());
        return "posts/details";
    }

    @GetMapping("/posts/new")
    public String createPostForm(Model model){
        model.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/posts/new")
    public String createPost(@ModelAttribute Post post, @RequestParam("new_tags") String tags){
        // Check if the author is included in the form data
//        if (post.getAuthor() != null) {
//            // Save the user to the database first
//            userService.saveUser(post.getAuthor());
//        }

//        List<Tag> tagList = Arrays.stream(tags.split(","))
//                .map(tagName -> tagService.createOrFetchTag(tagName.trim()))
//                .collect(Collectors.toList());
//
//        post.setTags(tagList);

        Set<Tag> tagSet = Arrays.stream(tags.split(","))
                .map(tagName -> tagService.createOrFetchTag(tagName.trim()))
                .collect(Collectors.toSet());
        post.setTags(tagSet);

        postService.savePost(post);
        return "redirect:/posts";
    }

//    @GetMapping("/update")
    @RequestMapping("/update")
    public String editPostForm(@RequestParam("id") Long id, Model model) {
        //Post post = postService.getPostById(id);
        Post post = postRepository.findById(id).get();
        //System.out.println(post.getTitle());
        //model.addAttribute(post);
//        System.out.println("Retrieved Post: " + post); // Print the retrieved post for debugging
//        if (post == null) {
//            // Handle the case where the post with the given ID does not exist
//            // Redirect to an error page or handle as appropriate for your application
//        }
        System.out.println("Post Content: " + post.getContent());

        model.addAttribute("post", post);
        model.addAttribute("postContent", post.getContent());

//        model.addAttribute("tags", post.getTags());
        return "posts/edit";

    }


    @RequestMapping("/savechange")
    public String editPost(@RequestParam("id") Long id, @ModelAttribute Post post) {
        Post newpost= postRepository.findById(id).get();
        newpost.setContent(post.getContent());
        newpost.setTitle(post.getTitle());
        newpost.setExcerpt(post.getExcerpt());
        System.out.println("update data");
        postRepository.save(newpost);

        //postService.savePost(post);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{postId}")
    public String editPost(@PathVariable long postId,
                           @RequestParam("title") String title,
                           @RequestParam("excerpt") String excerpt,
                           @RequestParam("tagg") String tagg,
                           @RequestParam("content") String content,
                           Model model){

        System.out.println(postId + " -> "+title + " -> " + excerpt + " -> " + content);

        Post post = postService.getPostById((long)postId);
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);

        // Convert tag names to Tag objects and set them in the post
//        List<Tag> tags = tagg.stream()
//                .map(tagService::createOrFetchTag)
//                .collect(Collectors.toList());
//        post.setTags(tags);

        Set<Tag> tagSet = Arrays.stream(tagg.split(","))
                .map(tagName -> tagService.createOrFetchTag(tagName.trim()))
                .collect(Collectors.toSet());
        post.setTags(tagSet);

        postService.savePost(post);

        return "redirect:/posts";
    }



    @GetMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }


    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam("comment") String comment) {

        System.out.println(name + " -> " + email + " -> " + comment);
        Post post = postService.getPostById(postId);

        System.out.println("Post: " + post);
        Comment newComment = new Comment();
        newComment.setName(name);
        newComment.setEmail(email);
        newComment.setComment(comment);
        newComment.setPost(post);

        post.addComment(newComment);

        postService.savePost(post);

        return "redirect:/posts/{postId}"; // Redirect to the post details page after commenting
    }


    @GetMapping("/posts/{postId}/comments/{commentId}/edit")
    public String editCommentForm(@PathVariable Long postId, @PathVariable Long commentId, Model model) {
        Comment comment = commentService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        model.addAttribute("post", comment.getPost()); // Add the associated post to the model
        return "comments/edit";
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String saveEditedComment(@PathVariable Long postId,
                                    @PathVariable Long commentId,
                                    @RequestParam("name") String name,
                                    @RequestParam("email") String email,
                                    @RequestParam("comment") String comment){

        Comment existingComment = commentService.getCommentById(commentId);

        existingComment.setName(name);
        existingComment.setEmail(email);
        existingComment.setComment(comment);

        commentService.saveComment(existingComment);

        return "redirect:/posts/{postId}";
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId){
        // Retrieve the post by ID
        Post post = postService.getPostById(postId);

        // Retrieve the comment by ID
        Comment comment = commentService.getCommentById(commentId);

        // Remove the comment from the post's comments list
        post.removeComment(comment);

        // update the post in the database (this will remove the comment due to CascadeType.REMOVE)
        postService.savePost(post);


        return "redirect:/posts/{postId}";
    }


    // Sorting

    @GetMapping("/posts/sortByDate")
    public String postsSortedByDate(Model model) {
        List<Post> posts = postService.getAllPostsSortedByDate();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/posts/sortByTitleAsc")
    public String postsSortedByTitleAsc(Model model){
        List<Post> posts = postService.getAllPostsSortedByTitleAsc();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/posts/sortByTitleDesc")
    public String postsSortedByTitleDesc(Model model){
        List<Post> posts = postService.getAllPostsSortedByTitleDesc();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/posts/sortByAuthorAsc")
    public String postsSortedByAuthorAsc(Model model) {
        List<Post> posts = postService.getAllPostsSortedByAuthorAsc();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/posts/sortByAuthorDesc")
    public String postsSortedByAuthorDesc(Model model) {
        List<Post> posts = postService.getAllPostsSortedByAuthorDesc();
        model.addAttribute("posts", posts);
        return "posts/list";
    }


    // Searching

    @GetMapping("/posts/search")
    public String searchPostsByTitle(@RequestParam("keyword") String keyword, Model model) {
        List<Post> searchResults = postService.searchPostsByTitle(keyword);
        model.addAttribute("posts", searchResults);
        return "posts/list";
    }

}
