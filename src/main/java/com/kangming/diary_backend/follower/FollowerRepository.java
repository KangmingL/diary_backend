package com.kangming.diary_backend.follower;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository <Follower, Long>{
    @Query(value = "SELECT f.followerId FROM Follower f WHERE f.userId = :userId")
    List<Long> getAllFollowersIdByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndFollowerId(Long userId, Long followerId);
}
