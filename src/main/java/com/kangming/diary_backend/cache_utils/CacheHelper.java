package com.kangming.diary_backend.cache_utils;

import static com.kangming.diary_backend.MyConstants.*;
import com.kangming.diary_backend.follower.FollowerRepository;
import com.kangming.diary_backend.following.FollowingRepository;
import com.kangming.diary_backend.post.Post;
import com.kangming.diary_backend.post.PostRepository;
import com.kangming.diary_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CacheHelper --- utility class that helps maintain Redis caching
 * @author Kangming Luo
 * */
@Component
public class CacheHelper {
    // ****************** REDIS TEMPLATE ******************
    private final RedisTemplate redisTemplate;

    // ****************** REPOSITORIES ******************
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowerRepository followerRepository;
    private final FollowingRepository followingRepository;

    public CacheHelper(@Qualifier("object_template") RedisTemplate redisTemplate, UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository, FollowingRepository followingRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
        this.followingRepository = followingRepository;
    }

    // ****************** DATABASE ACCESS FUNCTIONS ******************

    /**
     * Retrieve post from DB by post id.
     * @param postId a post id.
     * @return Post entity or null if it does not exist in DB.
     * */
    public Post getPostFromDB(Long postId){
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty())return null;
        return post.get();
    }

    /**
     * Retrieve HomeFeed list from DB given user id.
     * @param userId user id.
     * @return a list of post id in given user's HomeFeed.
     * */
    public List<Long> getHomeFeedPostIdsFromDB(Long userId){
        return postRepository.getHomeFeedByUserId(userId, FEED_CACHING_LIMIT);
    }

    /**
     * Retrieve UserFeed list from DB given user id.
     * @param userId user id.
     * @return a list of post id in given user's UserFeed.
     * */
    public List<Long> getUserFeedPostIdsFromDB(Long userId){
        return postRepository.getUserFeedByUserId(userId, FEED_CACHING_LIMIT);
    }

    /**
     * Retrieve celebrities' id from DB given user id.
     * @param userId user id.
     * @return a list of celebrities' id followed by the user.
     * */
    private List<Long> getAllCelebritiesFromDB(Long userId) {
        return followingRepository.getCelebritiesByUserId(userId);
    }

    // ****************** CACHE RELATED FUNCTIONS ******************

    /**
     * Store given Post entity to Redis cache
     * @param post A Post entity
     * */
    public void addPostToCache(Post post){
        String key = POST + post.getId();
        redisTemplate.opsForValue().set(key, post);
    }

    public void addPostToCacheById(Long id){
        Post post = getPostFromDB(id);
        addPostToCache(post);
    }

    /**
     * Get post from cache by post id. If post does not exist in cache, retrieve it from DB and cache it.
     * @param postId post id.
     * @return Post entity or null.
     * */
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

    /**
     * Retrieve HomeFeed from cache if existed, else create a new HomeFeed.
     * @param userId user id.
     * @return a list of post entities.
     * **/
    public List<Post> getHomeFeedFromCache(Long userId){
        String key = HOME_FEED + userId;

        // If HomeFeed does not exist, create a new one and cache it.
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "home");
        }else{
            // Add new posts from celebrities to home feed
            addNewPostsFromCelebrities(userId);
        }
        // Load post entities from HomeFeed cache.
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

    /**
     * Retrieve UserFeed from Redis if existed, else create a new UserFeed and cache it.
     * @param userId user id.
     * @return a list of post entities of user's UserFeed
     * */
    public List<Post> getUserFeedFromCache(Long userId){
        String key = USER_FEED + userId;
        // If not exist, create a new one and cache it.
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "user");
        }
        // Load actual post entities given ids.
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
     * Create a new Feed and cache it. New Feed can be either "home" or "user"; meaning HomeFeed or UserFeed.
     * @param userId userid.
     * @param type either "home" or "user"
     * */
    public void createFeed(Long userId, String type){
        // Get key
        String key = type.equals("user")? USER_FEED + userId: HOME_FEED + userId;
        Set<ZSetOperations.TypedTuple<Long>> feeds = new HashSet<>(); //Store post ids and scores (timestamp)
        List<Long> feedsId = type.equals("user")? getUserFeedPostIdsFromDB(userId): getHomeFeedPostIdsFromDB(userId);
        Map<String, Post> postsMap = new HashMap<>();
        for(int i=0; i< feedsId.size(); i++){
            // if post not in cache, load post from DB and cache it.
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
     * Retrieve recent posts from all a specific celebrity.
     * @param celebrityId celebrity id.
     * @param duration what to retrieve posts since duration timeunit ago.
     * @param timeUnit SECONDS, MINUTES, HOURS
     * @return a set of recent posts' id.
     * */
    private Set<Long> getRecentPostsFromCelebrity(Long celebrityId, long duration, TimeUnit timeUnit){
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Timestamp time = new Timestamp(currentTime.getTime() - timeUnit.MILLISECONDS.convert(duration, timeUnit));
        String key = USER_FEED + celebrityId;
        System.out.println(currentTime.getTime());
        System.out.println(timeUnit.convert(duration, TimeUnit.MILLISECONDS));
        if(redisTemplate.hasKey(key)){
            Set<Integer> newPosts = (Set<Integer>)redisTemplate.opsForZSet().reverseRangeByScore(key, time.getTime(), Long.MAX_VALUE);
            return newPosts.stream().map(Integer::longValue).collect(Collectors.toSet());
        }else{
            return postRepository.getPostsById(celebrityId, time, FEED_CACHING_LIMIT);
        }
    }

    /**
     * Retrieve and add new posts from celebrity followings to HomeFeed.
     * @param userId user id.
     * */
    private void addNewPostsFromCelebrities(Long userId) {
        List<Long> celebrities = getAllCelebritiesFromDB(userId);
        for(Long celebrityId: celebrities){
            Set<Long> recentPosts = getRecentPostsFromCelebrity(celebrityId, 30, TimeUnit.MINUTES);
            for(Long postId: recentPosts){
                addPostToHomeFeed(userId, getPostFromCache(postId));
            }
        }
    }







    /**
     * Add a post into HomeFeed if user's HomeFeed existed, else create a new HomeFeed.
     * @param userId user id.
     * @param newPost post entity want to be added to user's HomeFeed.
     * */
    public void addPostToHomeFeed(Long userId, Post newPost){
        String key = HOME_FEED + userId;
        // If home feed doesn't exist in cache, create a new one and cache it.
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "home");
        }else{
            // Add new post to HomeFeed if new post not in HomeFeed.
            redisTemplate.opsForZSet().addIfAbsent(key, newPost.getId(), newPost.getTimestamp().getTime());
            // If reach caching limit, remove old post from HomeFeed.
            if(redisTemplate.opsForZSet().zCard(key) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(key);
            }
        }
    }

    /**
     * Add a post to user's HomeFeed if HomeFeed already exists in cache.This is useful because sometimes we don't want
     * to cache inactive users.
     * @param userId user id.
     * @param newPost new post entity.
     * */
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
     * Add a post to user's UserFeed if UserFeed existed in cache. Else create a new UserFeed and cache it.
     * @param userId user id.
     * @param newPost new post entity.
     * */
    public void addPostToUserFeed(Long userId, Post newPost){
        String key = USER_FEED + userId;
        if(!redisTemplate.hasKey(key)){
            createFeed(userId, "user");
        }else{
            redisTemplate.opsForZSet().addIfAbsent(key, newPost.getId(), newPost.getTimestamp().getTime());
            if(redisTemplate.opsForZSet().zCard(key) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(key);
            }
        }
    }

    /**
     * Update user's HomeFeed when user follows a new user if HomeFeed exist in cache, else create a new HomeFeed and
     * cache it.
     * @param userId user id.
     * @param followingId user id which user followed.
     * */
    public void updateHomeFeed(Long userId, Long followingId) {
        String key = HOME_FEED + userId;
        // If HomeFeed already existed
        if(redisTemplate.hasKey(key)){
            String followingUserFeedKey = USER_FEED + followingId;
            // If following user's UserFeed doesn't exist in cache, create a new one and cache it.
            if(!redisTemplate.hasKey(followingUserFeedKey)){
                createFeed(followingId, "user");
            }
            // Retrieve posts from following user's UserFeed and update user's HomeFeed.
            redisTemplate.opsForZSet().intersectAndStore(key, followingUserFeedKey, key);
            while(redisTemplate.opsForZSet().zCard(followingUserFeedKey) > FEED_CACHING_LIMIT){
                redisTemplate.opsForZSet().popMin(followingUserFeedKey);
            }
        }else{
            // Create a new HomeFeed.
            createFeed(userId, "home");
        }
    }

    /**
     * Remove a specified post from cache.
     * @param postId post id that want to delete.
     * */
    public void removePostFromCache(Long postId) {
        String key = POST + postId;
        if(redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
        }
    }

    /**
     * When a regular user create a new post, push new post to active followers' HomeFeed.
     * @param userId user id.
     * @param postId new post id.
     * */
    public void pushPostToFollowers(Long userId, Long postId) {
        List<Long> followers = followerRepository.getAllFollowersIdByUserId(userId);
        for(Long followerId: followers){
            addPostToHomeFeedIfExist(followerId, getPostFromCache(postId));
        }
    }
}
