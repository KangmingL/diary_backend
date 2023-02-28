package com.kangming.diary_backend.following;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FollowingController --- Controller class used to handle requests relate to Following model.
 * @author Kangming Luo
 * */
@RestController
@RequestMapping(path = "api/following")
public class FollowingController {
    private FollowingService followingService;

    @Autowired
    public FollowingController(FollowingService followingService) {
        this.followingService = followingService;
    }

    // ****************** GET ******************
    @GetMapping("{userId}")
    public ResponseEntity<List<Long>> getFollowingById(@PathVariable("userId") Long userId){
        return followingService.getFollowingById(userId);
    }
}
