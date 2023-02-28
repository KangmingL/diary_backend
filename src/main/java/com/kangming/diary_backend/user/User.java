package com.kangming.diary_backend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kangming.diary_backend.follower.Follower;
import com.kangming.diary_backend.following.Following;
import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * User --- This is the User class that implements the User behaviour and properties.
 * @author Kangming Luo
 * */
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
    @JsonIgnore
    private String password;

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

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Follower> followers;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Following> followings;

    public User() {}

    public User(String userName, String email, Timestamp dob, String password, boolean isCelebrity) {
        this.userName = userName;
        this.email = email;
        this.dob = dob;
        this.followerCount = 0;
        this.followingCount = 0;
        this.isCelebrity = isCelebrity;
        this.password = password;
    }

    // ****************** HELPERS FUNCTIONS ******************
    public void decrementFollowingCount() {
        followingCount--;
    }

    public void decrementFollowerCount() {
        followerCount--;
    }

    public void incrementFollowingCount() {
        followingCount ++;
    }

    public void incrementFollowerCount() {
        followerCount ++;
    }


    // ****************** GETTERS AND SETTERS ******************
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

    public Timestamp getDob() {
        return dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", dob=" + dob + "}";
    }
}
