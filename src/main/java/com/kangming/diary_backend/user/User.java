package com.kangming.diary_backend.user;

import com.kangming.diary_backend.follower.Follower;
import com.kangming.diary_backend.following.Following;
import com.kangming.diary_backend.post.Post;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
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
    private LocalDate dob;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Follower> followers;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userId", orphanRemoval = true)
    private List<Following> followings;

    public User() {}

    public User(String userName, String email, LocalDate dob) {
        this.userName = userName;
        this.email = email;
        this.dob = dob;
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

    public LocalDate getDob() {
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

    public void setDob(LocalDate dob) {
        this.dob = dob;
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
