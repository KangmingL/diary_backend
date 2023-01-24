package com.kangming.diary_backend.following;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {
    @Query("SELECT f.followingId FROM Following f WHERE f.userId = :userId")
    List<Long> getFollowingsById(@Param("userId") Long userId);

    void deleteByUserIdAndFollowingId(Long userId, Long followingId);

}
