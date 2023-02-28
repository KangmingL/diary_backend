package com.kangming.diary_backend.follower;
import jakarta.persistence.*;

/**
 * Follower --- This class defines a user-follower relationship model in DB which contains a user and a follower.
 * @author Kangming Luo
 * */
@Table
@Entity
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "follower_id")
    private Long followerId;

    public Follower(Long userId, Long followerId) {
        this.userId = userId;
        this.followerId = followerId;
    }

    public Follower() {
    }

    // ****************** GETTERS AND SETTERS ******************

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }
}
