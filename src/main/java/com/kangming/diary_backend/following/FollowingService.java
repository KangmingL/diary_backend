package com.kangming.diary_backend.following;

import com.kangming.diary_backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingService {
    private final FollowingRepository followingRepository;

    @Autowired
    public FollowingService(FollowingRepository followingRepository) {
        this.followingRepository = followingRepository;
    }

    public List<Long> getFollowingById(Long userId) {
        List<Long> followings = followingRepository.getFollowingsById(userId);
        if(followings.size() == 0){
            throw new IllegalStateException("User with id "+userId+" has no followings!");
        }
        return followings;
    }
}
