package com.kangming.diary_backend.cache_utils;

import com.kangming.diary_backend.follower.FollowerRepository;
import com.kangming.diary_backend.following.FollowingRepository;
import com.kangming.diary_backend.post.Post;
import com.kangming.diary_backend.post.PostRepository;
import com.kangming.diary_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CacheHelper {

    String HOME_FEED = "homeFeed:";
    String USER_FEED = "userFeed:";
    int POSTS_CACHING_LIMIT = 30;
    String POST = "post:";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private FollowingRepository followingRepository;

    public CacheHelper() {
    }

    public void addPostToCache(Post post){
        String key = POST + post.getId();
        redisTemplate.opsForValue().set(key, post);
    }

    public Post getPostFromDB(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalStateException("Post with id " + postId + " does not exist!")
        );
        return post;
    }
    public Post getPostFromCache(Long postId){
        return (Post)redisTemplate.opsForValue().get(POST + postId);
    }

    public List<Long> getHomeFeedFromDB(Long userId){
        return postRepository.getHomeFeedByUserId(userId, POSTS_CACHING_LIMIT);
    }

    public List<Long> getUserFeedFromDB(Long userId){
        return postRepository.getUserFeedByUserId(userId, POSTS_CACHING_LIMIT);
    }

    public List<Post> getHomeFeedFromCache(Long userId){
        String key = HOME_FEED + userId;

        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "home");
        }
        Set<Integer> homeFeed = (Set<Integer>)redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        List<Post> posts = new ArrayList<>();
        for(Integer intId: homeFeed){
            Long postId = Long.valueOf(intId);
            posts.add(getPostFromCache(postId));
        }
        return posts;
    }

    public List<Post> getUserFeedFromCache(Long userId){
        String key = USER_FEED + userId;
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "user");
        }
        Set<Integer> postList = (Set<Integer>) redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        List<Post> posts = new ArrayList<>();
        for(Integer intId: postList){
            Long postId = Long.valueOf(intId);
            posts.add(getPostFromCache(postId));
        }
        return posts;
    }

    public void createFeed(Long userId, String type){
        String key = type.equals("user")? USER_FEED + userId: HOME_FEED + userId;
        Set<ZSetOperations.TypedTuple<Long>> feeds = new HashSet<>();
        List<Long> feedsId = type.equals("user")? getUserFeedFromDB(userId): getHomeFeedFromDB(userId);
        Map<String, Post> postsMap = new HashMap<>();
        for(int i=0; i< feedsId.size(); i++){
            // if post not in cache
            Long postId = feedsId.get(i);
            Post post = getPostFromDB(postId);
            if(!redisTemplate.hasKey(POST + feedsId.get(i))){
                // add to cache
                postsMap.put(POST + postId, post);
            }
            feeds.add(new DefaultTypedTuple<>(postId, (double) post.getTimestamp().getTime()));
        }
        redisTemplate.opsForValue().multiSet(postsMap);
        if(feeds.size() != 0){
            redisTemplate.opsForZSet().add(key, feeds);
        }
    }

    public void addPostToHomeFeed(Long userId, Post newPost){
        String key = HOME_FEED + userId;
        // If home feed doesnt exist in cache
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "home");
        }else{
            redisTemplate.opsForZSet().add(key, newPost.getId(), newPost.getTimestamp().getTime());
        }
    }

    public void addPostToUserFeed(Long userId, Post newPost){
        String key = USER_FEED + userId;
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "user");
        }else{
            redisTemplate.opsForZSet().add(key, newPost.getId(), newPost.getTimestamp().getTime());
        }
    }
}
