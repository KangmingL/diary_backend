package com.kangming.diary_backend.following;

import jakarta.persistence.*;

/**
 * Following --- This class defines a user-following relationship model in DB which contains a user and a following user.
 * @author Kangming Luo
 * */
@Table
@Entity
public class Following {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "following_id")
    private Long followingId;

    public Following(Long userId, Long followingId) {
        this.userId = userId;
        this.followingId = followingId;
    }

    public Following() {
    }

    // ****************** GETTERS AND SETTERS ******************
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}
