package com.kangming.diary_backend.relationship;

import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "api/relationship")
public class RelationshipController {
    private final RelationshipService relationshipService;

    @Autowired
    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    @PostMapping("{userId}/follow/{targetId}")
    public ResponseEntity<String> follow(
            @PathVariable("userId") Long userId,
            @PathVariable("targetId") Long targetId
    ){
        return relationshipService.follow(userId, targetId);
    }

    @DeleteMapping("{userId}/unfollow/{targetId}")
    public ResponseEntity<String> unfollow(
        @PathVariable("userId") Long userId,
        @PathVariable("targetId") Long targetId
    ){
        return relationshipService.unfollow(userId, targetId);
    }
}
