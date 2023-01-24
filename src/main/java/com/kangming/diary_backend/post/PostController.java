package com.kangming.diary_backend.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ****************** CREATE POST ******************
    @PostMapping("user/{userId}/post")
    public ResponseEntity<Post> newPost(
            @RequestBody Post newPost,
            @PathVariable("userId") Long userId
    ){
        return postService.createNewPost(newPost, userId);
    }

    // ****************** DELETE POST BY ID******************
    @DeleteMapping("post/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable("postId") Long postId
    ){
        return postService.deletePostById(postId);
    }

    // ****************** GET POST BY ID ******************
//    @Cacheable("post")
    @GetMapping("post/{postId}")
    public Post getPost(
            @PathVariable("postId") Long postId
    ){
        return postService.getPostById(postId);
    }

    // ****************** GET POSTS BY USERID ******************
    @GetMapping("user/{userId}/posts")
    public ResponseEntity<List<Post>> getPostsByUserId(
            @PathVariable("userId") Long userId
    ){
        return postService.getPostsByUserId(userId);
    }

    // ****************** GET USER FEED ******************
    @PostMapping("user/{userId}/userfeed")
    public ResponseEntity<List<Post>> getUserFeed(
            @PathVariable("userId") Long userId
    ){
        return postService.getUserFeed(userId);
    }

    // ****************** GET HOME FEED ******************
    @PostMapping("user/{userId}/homefeed")
    public ResponseEntity<List<Post>> getHomeFeed(
            @PathVariable("userId") Long userId
    ){
        return postService.getHomeFeed(userId);
    }

    // ****************** GET ALL POSTS ******************
    @GetMapping("post")
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }
}