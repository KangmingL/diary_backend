package com.kangming.diary_backend.user;

import com.kangming.diary_backend.follower.Follower;
import com.kangming.diary_backend.following.Following;
import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity(name = "users")
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )

    @Column
    private long id;

    @Column
    private String userName;
    @Column
    private String email;
    @Column
    private Timestamp dob;

    @Column
    private boolean isCelebrity;

    @Column
    private int followerCount;
    @Column
    private int followingCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Follower> followers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Following> followings;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
//    private List<Post> posts;
    public User() {}

    public User(String userName, String email, Timestamp dob, boolean isCelebrity) {
        this.userName = userName;
        this.email = email;
        this.dob = dob;
        this.followerCount = 0;
        this.followingCount = 0;
        this.isCelebrity = isCelebrity;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public List<Following> getFollowings() {
        return followings;
    }

    public void setFollowings(List<Following> followings) {
        this.followings = followings;
    }

    public Timestamp getDob() {
        return dob;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDob(Timestamp dob) {
        this.dob = dob;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public boolean isCelebrity() {
        return isCelebrity;
    }

    public void setCelebrity(boolean celebrity) {
        isCelebrity = celebrity;
    }

    public void incrementFollowingCount() {
        followingCount ++;
    }

    public void incrementFollowerCount() {
        followerCount ++;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", dob=" + dob + "}";
    }


    public void decrementFollowingCount() {
        followingCount--;
    }

    public void decrementFollowerCount() {
        followerCount--;
    }
}
