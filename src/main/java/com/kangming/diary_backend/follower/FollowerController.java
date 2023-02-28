package com.kangming.diary_backend.follower;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FollowerController --- Controller class used to handle requests relate to Follower model.
 * @author Kangming Luo
 * */
@RestController
@RequestMapping(path = "api/follower")
public class FollowerController {
    // Follower service
    private final FollowerService followerService;

    @Autowired
    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    // ****************** GET ******************
    @GetMapping(path = "{userId}")
    public ResponseEntity<List<Long>> getFollowersById(@PathVariable("userId") Long userId){
        return followerService.getFollowersById(userId);
    }
}
