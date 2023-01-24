package com.kangming.diary_backend.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);

    @Query(value = "SELECT id FROM post " +
            "WHERE user_id in (SELECT following_id FROM following WHERE following.user_id = ?1)" +
            " or user_id = ?1 " +
            "ORDER BY post.timestamp DESC" +
            " FETCH FIRST ?2 ROWS ONLY "
            , nativeQuery = true)
    List<Long> getHomeFeedByUserId(Long userId, int limit);

    @Query(value = "SELECT id FROM post " +
            "WHERE post.user_id = ?1 " +
            "ORDER BY post.timestamp DESC " +
            "FETCH FIRST ?2 ROWS ONLY "
            ,nativeQuery = true)
    List<Long> getUserFeedByUserId(Long userId, int limit);
}
