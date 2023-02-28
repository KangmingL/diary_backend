package com.kangming.diary_backend.follower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * FollowerService --- Service class that is used to handle specific backend logics.
 * @author Kangming Luo
 * */
@Service
public class FollowerService {
    private final FollowerRepository followerRepository;

    @Autowired
    public FollowerService(FollowerRepository followerRepository) {
        this.followerRepository = followerRepository;
    }

    /**
     * Get all the followers of specified user.
     * @param userId user id.
     * @return http response contains all the followers' id.
     * */
    public ResponseEntity<List<Long>> getFollowersById(Long userId) {
        List<Long> followers = followerRepository.getAllFollowersIdByUserId(userId);
        // No followers
        if(followers.size() == 0){
            return new ResponseEntity<>(new ArrayList<Long>(), HttpStatus.OK);
        }
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }
}
