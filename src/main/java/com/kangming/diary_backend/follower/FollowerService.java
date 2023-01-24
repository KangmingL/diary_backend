package com.kangming.diary_backend.follower;

import com.kangming.diary_backend.following.FollowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class FollowerService {
    private final FollowerRepository followerRepository;

    @Autowired
    public FollowerService(FollowerRepository followerRepository) {
        this.followerRepository = followerRepository;
    }

    public List<Long> getFollowersById(Long userId) {

        List<Long> followers = followerRepository.getAllFollowersIdByUserId(userId);
        if(followers.size() == 0){
            throw new IllegalStateException("User with ID "+userId+" has no followers!");
        }
        System.out.println(followers);
        return followers;
    }
}
