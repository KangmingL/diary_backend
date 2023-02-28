package com.kangming.diary_backend.relationship;

import com.kangming.diary_backend.cache_utils.CacheHelper;
import com.kangming.diary_backend.follower.Follower;
import com.kangming.diary_backend.follower.FollowerRepository;
import com.kangming.diary_backend.following.Following;
import com.kangming.diary_backend.following.FollowingRepository;
import com.kangming.diary_backend.user.User;
import com.kangming.diary_backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import static com.kangming.diary_backend.MyConstants.CELEBRITY_THRESHOLD;

import java.util.Optional;

/**
 * RelationshipService --- Service class that implements backend logics including follow logic and unfollow logic.
 * @author Kangming Luo
 * */
@Service
public class RelationshipService {
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final CacheHelper cacheHelper;
    @Autowired
    public RelationshipService(FollowingRepository followingRepository,
                               FollowerRepository followerRepository,
                               UserRepository userRepository,
                               CacheHelper cacheHelper) {
        this.followingRepository = followingRepository;
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.cacheHelper = cacheHelper;
    }

    /**
     * Allow user to follow a target user.
     * @param userId user id.
     * @param targetId target user id.
     * */
    @Transactional
    public ResponseEntity<String> follow(Long userId, Long targetId){
        Follower follower = new Follower(targetId, userId);
        Following following = new Following(userId, targetId);
        followerRepository.save(follower);
        followingRepository.save(following);
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ResponseEntity<String>("User with "+userId+" does not exist!", HttpStatus.BAD_REQUEST);
        }
        Optional<User> targetUserOptional = userRepository.findById(targetId);
        if(targetUserOptional.isEmpty()){
            return new ResponseEntity<String>("User with "+targetId+" does not exist!", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        User targetUser = targetUserOptional.get();
        // Update following count and follower count.
        user.incrementFollowingCount();
        targetUser.incrementFollowerCount();
        if(targetUser.getFollowerCount() >= CELEBRITY_THRESHOLD)targetUser.setCelebrity(true);
        cacheHelper.updateHomeFeed(userId, targetId);

        return new ResponseEntity<>("user "+ userId + " successfully followed "+targetId, HttpStatus.CREATED);
    }

    /**
     * Allow a user to unfollow a target user.
     * @param userId user id
     * @param targetId target user id
     * @return return a response entity contains message
     * */
    @Transactional
    public ResponseEntity<String> unfollow(Long userId, Long targetId) {
        followingRepository.deleteByUserIdAndFollowingId(userId, targetId);
        followerRepository.deleteByUserIdAndFollowerId(targetId, userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ResponseEntity<String>("User with "+userId+" does not exist!", HttpStatus.BAD_REQUEST);
        }
        Optional<User> targetUserOptional = userRepository.findById(targetId);
        if(targetUserOptional.isEmpty()){
            return new ResponseEntity<String>("User with "+targetId+" does not exist!", HttpStatus.BAD_REQUEST);
        }
        User user = userOptional.get();
        User targetUser = targetUserOptional.get();
        user.decrementFollowingCount();
        targetUser.decrementFollowerCount();
        if(targetUser.getFollowerCount() < CELEBRITY_THRESHOLD)targetUser.setCelebrity(false);
        return new ResponseEntity<>(
                "User "+userId+" successfully unfollowed "+targetId, HttpStatus.OK
        );
    }
}
