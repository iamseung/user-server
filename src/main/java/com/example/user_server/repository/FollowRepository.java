package com.example.user_server.repository;

import com.example.user_server.dto.UserInfo;
import com.example.user_server.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

    // 단건 조회
    // followerId 의 User 가 userId 의 User 를 follow 하고 있음을 의미
    Follow findByUserIdAndFollowerId(int userId, int followerId);

    // 팔로워 목록 조회(나를 팔로우 하는 사람들), JPQL
    @Query(value = "SELECT new com.example.user_server.dto.UserInfo(u.userId, u.username, u.email) FROM Follow f, User u WHERE f.userId = :userId and u.userId = f.followerId")
    List<UserInfo> findFollowersByUserId(@Param("userId") int userId);

    // 팔로잉 목록 조회(내가 팔로우 하는 사람들), JPQL
    @Query(value = "SELECT new com.example.user_server.dto.UserInfo(u.userId, u.username, u.email) FROM Follow f, User u WHERE f.followerId = :userId and u.userId = f.userId")
    List<UserInfo> findFollowingByUserId(@Param("userId") int userId);
}
