package com.kangming.diary_backend.following;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/following")
public class FollowingController {
    private FollowingService followingService;

    @Autowired
    public FollowingController(FollowingService followingService) {
        this.followingService = followingService;
    }

    @GetMapping("{userId}")
    public List<Long> getFollowingById(@PathVariable("userId") Long userId){
        return followingService.getFollowingById(userId);
    }
}
