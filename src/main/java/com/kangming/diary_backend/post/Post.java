package com.kangming.diary_backend.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kangming.diary_backend.user.User;
import jakarta.persistence.*;

import java.sql.Timestamp;


@Entity(name = "post")
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )
    private Long id;
    private String content;
    private Timestamp timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Post(String content, Timestamp timestamp, User user) {
        this.content = content;
        this.timestamp = timestamp;
        this.user = user;
    }

    public Post() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", context='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
