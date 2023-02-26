package com.kangming.diary_backend.post;

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
//    @JsonView(View.Summary.class)
    private Long id;
//    @JsonView(View.Summary.class)
    private String content;
//    @JsonView(View.Summary.class)
    private Timestamp timestamp;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "user_id", nullable = false)
//    @JsonView(View.Summary.class)
//    @JsonIgnore
//    private User user;
    @Column(name = "user_id")
    private Long userId;

    public Post(String content, Timestamp timestamp, User user) {
        this.content = content;
        this.timestamp = timestamp;
//        this.user = user;
        this.userId = user.getId();
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

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }

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
