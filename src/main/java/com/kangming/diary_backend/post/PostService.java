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
import java.util.Optional;

/**
 * PostService --- service class that used to handle specific post services. This class only talk to cache, and cache
 * is responsible to talk to DB.
 * @author Kangming Luo
 * */
@Service
public class PostService {

    // ****************** REPOSITORIES ******************
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CacheHelper cacheHelper;

    @Autowired
    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CacheHelper cacheHelper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.cacheHelper = cacheHelper;
    }

    // ****************** CREATE ******************
    /**
     * Create a new post given post entity and user id.
     * @param newPost new post entity.
     * @param userId user id.
     * @return a http response.
     * */
    @Transactional
    public ResponseEntity<String> createNewPost(Post newPost, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        newPost.setTimestamp(timestamp);
        newPost.setUserId(user.getId());

        // Update DB
        postRepository.saveAndFlush(newPost);

        // Cache new post
        cacheHelper.addPostToCache(newPost);

        // Update or create homeFeed
        cacheHelper.addPostToHomeFeed(userId, newPost);
        cacheHelper.addPostToUserFeed(userId, newPost);

        // If user is a normal user, then push the post to his followers
        if(!user.isCelebrity()){
            cacheHelper.pushPostToFollowers(userId, newPost.getId());
        }
        return new ResponseEntity<>("Successfully create a new post!", HttpStatus.CREATED);
    }

    // ****************** GET ******************
    /**
     * Get post by post id from cache.
     * @param postId post id.
     * @return post entity.
     * */
    public ResponseEntity<Post> getPostById(Long postId) {
        if(postRepository.existsById(postId)){
            return new ResponseEntity<>(cacheHelper.getPostFromCache(postId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get HomeFeed from cache.
     * @param userId user id.
     * @return http response.
     * */
    public ResponseEntity<List<Post>> getHomeFeed(Long userId){
        if(userRepository.existsById(userId)){
            return new ResponseEntity<>(cacheHelper.getHomeFeedFromCache(userId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get UserFeed from cache.
     * @param userId user id.
     * @return http response contains a list of posts.
     * */
    public ResponseEntity<List<Post>> getUserFeed(Long userId){
        if(userRepository.existsById(userId)){
            return new ResponseEntity<>(cacheHelper.getUserFeedFromCache(userId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all posts of the given user.
     * @param userId given user id.
     * @return http response contains a list of posts.
     * */
    public ResponseEntity<List<Post>> getPostsByUserId(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if(!exists){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        List<Post> posts = postRepository.findAllByUserId(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * Get all posts in DB. Used for testing.
     * */
    public ResponseEntity<List<Post>> getAllPosts() {
        return new ResponseEntity<>(postRepository.findAll(), HttpStatus.OK);
    }

    // ****************** DELETE ******************
    /**
     * Remove post from DB and cache.
     * @param postId post id.
     * @return response entity contains a status message.
     * */
    @Transactional
    public ResponseEntity<String> deletePostById(Long postId) {
        if(postRepository.existsById(postId)){
            this.postRepository.deleteById(postId);
        }else {
            return new ResponseEntity<>("Post does not exist!", HttpStatus.BAD_REQUEST);
        }
        cacheHelper.removePostFromCache(postId);
        return new ResponseEntity<>("Successfully deleted post with id: "+postId, HttpStatus.OK);
    }
}
