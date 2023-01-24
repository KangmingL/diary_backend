package com.kangming.diary_backend.relationship;

import com.kangming.diary_backend.follower.Follower;
import com.kangming.diary_backend.follower.FollowerRepository;
import com.kangming.diary_backend.following.Following;
import com.kangming.diary_backend.following.FollowingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationshipService {
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    @Autowired
    public RelationshipService(FollowingRepository followingRepository, FollowerRepository followerRepository) {
        this.followingRepository = followingRepository;
        this.followerRepository = followerRepository;
    }

    public ResponseEntity<String> follow(Long userId, Long targetId){
        Follower follower = new Follower(targetId, userId);
        Following following = new Following(userId, targetId);
        followerRepository.save(follower);
        followingRepository.save(following);
        return new ResponseEntity<>("user "+ userId + " successfully followed "+targetId, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<String> unfollow(Long userId, Long targetId) {
        followingRepository.deleteByUserIdAndFollowingId(userId, targetId);
        followerRepository.deleteByUserIdAndFollowerId(targetId, userId);
        return new ResponseEntity<>(
                "user "+userId+" unfollowed "+targetId, HttpStatus.OK
        );
    }
}
