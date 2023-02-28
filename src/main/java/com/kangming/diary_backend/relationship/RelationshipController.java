package com.kangming.diary_backend.relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RelationshipController --- Used to handler REST requests related to follow and unfollow.
 * @author Kangming Luo
 * */
@RestController
@RequestMapping(path = "api/relationship")
public class RelationshipController {
    private final RelationshipService relationshipService;

    @Autowired
    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    // ****************** POST ******************
    @PostMapping("{userId}/follow/{targetId}")
    public ResponseEntity<String> follow(
            @PathVariable("userId") Long userId,
            @PathVariable("targetId") Long targetId
    ){
        return relationshipService.follow(userId, targetId);
    }

    // ****************** DELETE ******************
    @DeleteMapping("{userId}/unfollow/{targetId}")
    public ResponseEntity<String> unfollow(
        @PathVariable("userId") Long userId,
        @PathVariable("targetId") Long targetId
    ){
        return relationshipService.unfollow(userId, targetId);
    }
}
