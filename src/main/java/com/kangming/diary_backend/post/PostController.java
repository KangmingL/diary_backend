package com.kangming.diary_backend.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Post Controller --- This is the post controller class in MCV design structure that used to handle RESTful requests.
 * @author Kangming Luo
 * */
@RestController
@RequestMapping(path = "api/")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ****************** POST ******************
    @PostMapping("user/{userId}/post")
    public ResponseEntity<String> newPost(
            @RequestBody Post newPost,
            @PathVariable("userId") Long userId
    ){
        return postService.createNewPost(newPost, userId);
    }

    // ****************** GET ******************
    @GetMapping("post/{postId}")
    public ResponseEntity<Post> getPost(
            @PathVariable("postId") Long postId
    ){
        return postService.getPostById(postId);
    }

    @GetMapping("user/{userId}/posts")
    public ResponseEntity<List<Post>> getPostsByUserId(
            @PathVariable("userId") Long userId
    ){
        return postService.getPostsByUserId(userId);
    }

    @GetMapping("user/{userId}/userfeed")
    public ResponseEntity<List<Post>> getUserFeed(
            @PathVariable("userId") Long userId
    ){
        return postService.getUserFeed(userId);
    }

    @GetMapping("user/{userId}/homefeed")
    public ResponseEntity<List<Post>> getHomeFeed(
            @PathVariable("userId") Long userId
    ){
        return postService.getHomeFeed(userId);
    }

    @GetMapping("post")
    public ResponseEntity<List<Post>> getAllPosts(){
        return postService.getAllPosts();
    }

    // ****************** DELETE ******************
    @DeleteMapping("post/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable("postId") Long postId
    ){
        return postService.deletePostById(postId);
    }


}
