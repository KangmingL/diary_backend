package com.kangming.diary_backend.post;

import com.kangming.diary_backend.user.User;
import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Post --- This is the Post class that defines the post behaviors and properties.
 * @author Kangming Luo
 * */
@Entity(name = "post")
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )
    @Column
    private Long id;

    @Column
    private String content;

    @Column
    private Timestamp timestamp;

    @Column(name = "user_id")
    private Long userId;

    public Post(String content, Timestamp timestamp, User user) {
        this.content = content;
        this.timestamp = timestamp;
        this.userId = user.getId();
    }

    public Post() {
    }

    // ****************** GETTERS AND SETTERS ******************
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
