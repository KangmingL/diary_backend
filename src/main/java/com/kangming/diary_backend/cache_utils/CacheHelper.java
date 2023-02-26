package com.kangming.diary_backend.cache_utils;

import static com.kangming.diary_backend.MyConstants.*;
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

    public CacheHelper() {}

    public void addPostToCache(Post post){
        String key = POST + post.getId();
        redisTemplate.opsForValue().set(key, post);
    }

    /**
     * Retrieve post from db by post id
     * return null if post does not exist
     * **/
    public Post getPostFromDB(Long postId){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty())return null;
        return post.get();
    }

    public void addPostToCacheById(Long id){
        Post post = getPostFromDB(id);
        addPostToCache(post);
    }

    /**
     * Get post from cache by post id.
     * If post does not exist in cache, then retrieve from DB and add to cache.
     * return null if post does not exist in DB.
     * **/
    public Post getPostFromCache(Long postId){
        String key = POST + postId;
        if(redisTemplate.hasKey(key)){
            return (Post)redisTemplate.opsForValue().get(POST + postId);
        }else{
            Post post = getPostFromDB(postId);
            if(post == null)return null;
            addPostToCache(post);
            return post;
        }
    }

    public List<Long> getHomeFeedFromDB(Long userId){
        return postRepository.getHomeFeedByUserId(userId, FEED_CACHING_LIMIT);
    }

    public List<Long> getUserFeedFromDB(Long userId){
        return postRepository.getUserFeedByUserId(userId, FEED_CACHING_LIMIT);
    }

    /**
     * Retrieve HomeFeed from Redis if existed, else create a new HomeFeed
     * **/
    public List<Post> getHomeFeedFromCache(Long userId){
        String key = HOME_FEED + userId;

        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "home");
        }else{
            // Retrieve any new posts by celebrities followings
            getNewPostsFromCelebrities(userId);
        }
        Set<Integer> homeFeed = (Set<Integer>)redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        List<Post> posts = new ArrayList<>();
        for(Integer intId: homeFeed){
            Long postId = Long.valueOf(intId);
            Post post = getPostFromCache(postId);
            if(post == null)continue;
            posts.add(getPostFromCache(postId));
        }
        return posts;
    }

    private void getNewPostsFromCelebrities(Long userId) {
        List<Long> celebrities = getAllCelebritiesFromDB(userId);
        for(Long celebrityId: celebrities){
            System.out.println(celebrityId);
        }
    }

    private List<Long> getAllCelebritiesFromDB(Long userId) {
        return followingRepository.getCelebritiesByUserId(userId);
    }

    /**
     * Retrieve UserFeed from Redis if existed, else create a new UserFeed
     * **/
    public List<Post> getUserFeedFromCache(Long userId){
        String key = USER_FEED + userId;
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "user");
        }
        Set<Integer> postList = (Set<Integer>) redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        List<Post> posts = new ArrayList<>();
        for(Integer intId: postList){
            Long postId = Long.valueOf(intId);
            Post post = getPostFromCache(postId);
            if(post == null)continue;
            posts.add(getPostFromCache(postId));
        }
        return posts;
    }

    /**
     * Create a new Feed in Redis. It can be either "home" or "user"; meaning HomeFeed or UserFeed.
     * **/
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

    /**
     * Add a post into HomeFeed if existed, else create a new HomeFeed
     * **/
    public void addPostToHomeFeed(Long userId, Post newPost){
        String key = HOME_FEED + userId;
        // If home feed doesnt exist in cache
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "home");
        }else{
            redisTemplate.opsForZSet().add(key, newPost.getId(), newPost.getTimestamp().getTime());
            if(redisTemplate.opsForZSet().zCard(key) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(key);
            }
        }
    }

    public void addPostToHomeFeedIfExist(Long userId, Post newPost){
        String key = HOME_FEED + userId;
        if(redisTemplate.hasKey(key)){
            redisTemplate.opsForZSet().add(key, newPost.getId(), newPost.getTimestamp().getTime());
            if(redisTemplate.opsForZSet().zCard(key) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(key);
            }
        }
    }

    /**
     * Add a post to specified UserFeed is existed. Else create a new UserFeed.
     * **/
    public void addPostToUserFeed(Long userId, Post newPost){
        String key = USER_FEED + userId;
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "user");
        }else{
            redisTemplate.opsForZSet().add(key, newPost.getId(), newPost.getTimestamp().getTime());
            if(redisTemplate.opsForZSet().zCard(key) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(key);
            }
        }
    }

    /**
     * Update user's HomeFeed when user follows a new user, else create a new HomeFeed
     * **/
    public void updateHomeFeed(Long userId, Long followingId) {
        String key = HOME_FEED + userId;
        // If HomeFeed already existed
        if(redisTemplate.hasKey(key)){
            Long feedSize = redisTemplate.opsForZSet().zCard(key);
            String followingUserFeedKey = USER_FEED + followingId;
            if(!redisTemplate.hasKey(followingUserFeedKey)){
                createFeed(followingId, "user");
            }
            redisTemplate.opsForZSet().intersectAndStore(key, followingUserFeedKey, key);
            while(redisTemplate.opsForZSet().zCard(followingUserFeedKey) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(followingUserFeedKey);
            }
        }else{
            createFeed(userId, "home");
        }
    }

    public void removePostFromCache(Long postId) {
        String key = POST + postId;
        if(redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
        }
    }

    /**
     * Push post to followers if followers are active.
     * **/
    public void pushPostToFollowers(Long userId, Long postId) {
        List<Long> followers = followerRepository.getAllFollowersIdByUserId(userId);
        for(Long followerId: followers){
            addPostToHomeFeedIfExist(followerId, getPostFromCache(postId));
        }
    }
}
