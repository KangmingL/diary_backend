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

import java.util.List;

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

    @Transactional
    public ResponseEntity<String> follow(Long userId, Long targetId){
        Follower follower = new Follower(targetId, userId);
        Following following = new Following(userId, targetId);
        followerRepository.save(follower);
        followingRepository.save(following);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User with id "+userId+" does not exists!")
        );
        User targetUser = userRepository.findById(targetId).orElseThrow(
                () -> new IllegalStateException("User with id "+targetId+" does not exists!")
        );
//        user.getFollowings().add(following);
//        targetUser.getFollowers().add(follower);
//        userRepository.saveAll(List.of(user, targetUser));
        user.incrementFollowingCount();
        targetUser.incrementFollowerCount();
        if(targetUser.getFollowerCount() >= CELEBRITY_THRESHOLD)targetUser.setCelebrity(true);
        cacheHelper.updateHomeFeed(userId, targetId);

        return new ResponseEntity<>("user "+ userId + " successfully followed "+targetId, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> unfollow(Long userId, Long targetId) {
        followingRepository.deleteByUserIdAndFollowingId(userId, targetId);
        followerRepository.deleteByUserIdAndFollowerId(targetId, userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User with id "+userId+" does not exists!")
        );
        User targetUser = userRepository.findById(targetId).orElseThrow(
                () -> new IllegalStateException("User with id "+targetId+" does not exists!")
        );
        user.decrementFollowingCount();
        targetUser.decrementFollowerCount();
        if(targetUser.getFollowerCount() < CELEBRITY_THRESHOLD)targetUser.setCelebrity(false);
        return new ResponseEntity<>(
                "user "+userId+" unfollowed "+targetId, HttpStatus.OK
        );
    }
}
