package com.kangming.diary_backend.following;

import com.kangming.diary_backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * FollowingService -- Service class that implements backend logics related to user-following relationship.
 * @author Kangming Luo
 * */
@Service
public class FollowingService {
    private final FollowingRepository followingRepository;

    @Autowired
    public FollowingService(FollowingRepository followingRepository) {
        this.followingRepository = followingRepository;
    }

    /**
     * Get all the following users of specified user.
     * @param userId user id
     * @return response entity contains all the following users' id.
     * */
    public ResponseEntity<List<Long>> getFollowingById(Long userId) {
        List<Long> followings = followingRepository.getFollowingsById(userId);
        if(followings.size() == 0){
            return new ResponseEntity<>(new ArrayList<Long>(), HttpStatus.OK);
        }
        return new ResponseEntity<>(followings, HttpStatus.OK);
    }
}
