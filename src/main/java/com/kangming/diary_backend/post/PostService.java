package com.kangming.diary_backend.post;

import com.kangming.diary_backend.cache_utils.CacheHelper;
import com.kangming.diary_backend.user.User;
import com.kangming.diary_backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private CacheHelper cacheHelper;
    private String POST_KEY = "post:";
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, CacheHelper cacheHelper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cacheHelper = cacheHelper;
    }


    @Transactional
    public ResponseEntity<Post> createNewPost(Post newPost, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User with id "+userId+" does not exists!")
        );
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newPost.setTimestamp(timestamp);
        newPost.setUser(user);
        // Update DB
        postRepository.saveAndFlush(newPost);

        // Cache new post
        cacheHelper.addPostToCache(newPost);
        // Update or create homeFeed
        cacheHelper.addPostToHomeFeed(userId, newPost);
        cacheHelper.addPostToUserFeed(userId, newPost);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> deletePostById(Long postId) {
        if(postRepository.existsById(postId)){
            this.postRepository.deleteById(postId);
        }else {
            throw new IllegalStateException("post with id " + postId + " does not exist!");
        }
        return new ResponseEntity<>("Successfully deleted post with id: "+postId, HttpStatus.OK);
    }

    public Post getPostById(Long postId) {
        String key = POST_KEY + postId;
        if(redisTemplate.hasKey(key)){
            return (Post)redisTemplate.opsForValue().get(key);
        }else{
            Post post = postRepository.findById(postId).orElseThrow(
                    () -> new IllegalStateException("Post with id "+postId + " does not exists!")
            );
            redisTemplate.opsForValue().set(key, post);
            return post;
        }
    }

    public ResponseEntity<List<Post>> getUserFeed(Long userId){
        if(userRepository.existsById(userId)){
            return new ResponseEntity<>(cacheHelper.getUserFeedFromCache(userId), HttpStatus.OK);
        }else{
            throw new IllegalStateException("User with id "+userId+" does not exist!");
        }
    }

    public ResponseEntity<List<Post>> getHomeFeed(Long userId){
        if(userRepository.existsById(userId)){
            return new ResponseEntity<>(cacheHelper.getHomeFeedFromCache(userId), HttpStatus.OK);
        }else{
            throw new IllegalStateException("User with id "+userId+" does not exist!");
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public ResponseEntity<List<Post>> getPostsByUserId(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if(!exists){
            throw new IllegalStateException("User with id:"+userId+" does not exists!");
        }
        List<Post> posts = postRepository.findAllByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
