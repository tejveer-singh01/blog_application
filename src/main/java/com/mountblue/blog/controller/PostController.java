package com.mountblue.blog.controller;

import com.mountblue.blog.entitites.Post;
import com.mountblue.blog.entitites.User;
import com.mountblue.blog.repository.PostRepository;
import com.mountblue.blog.service.PostService;
import com.mountblue.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {
    @Autowired
    PostRepository postRepository;
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService){
        this.postService = postService;
        this.userService = userService;
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
        return "posts/details";
    }

    @GetMapping("/posts/new")
    public String createPostForm(Model model){
        model.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/posts/new")
    public String createPost(@ModelAttribute Post post){
        // Check if the author is included in the form data
        if (post.getAuthor() != null) {
            // Save the user to the database first
            userService.saveUser(post.getAuthor());
        }


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
        model.addAttribute("post", post);
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
                           @RequestParam("content") String content){

        System.out.println(postId + " -> "+title + " -> " + excerpt + " -> " + content);

        Post post = postService.getPostById((long)postId);
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);
        postService.savePost(post);

        return "redirect:/posts";
    }

//    @PostMapping("/posts/{id}/edit")
//    public String editPost(@PathVariable Long id, @ModelAttribute Post post) {
//        postService.savePost(post);
//        return "redirect:/posts";
//    }

    @GetMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }





}
