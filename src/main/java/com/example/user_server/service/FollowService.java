package com.example.user_server.service;

import com.example.user_server.dto.UserInfo;
import com.example.user_server.entity.Follow;
import com.example.user_server.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {
    private FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public boolean isFollow(int userId, int followerId) {
        // followerId 의 User 가 userId 의 User 를 follow 하고 있음을 의미
        Follow follow = followRepository.findByUserIdAndFollowerId(userId, followerId);

        return follow != null;
    }

    // Follow 기능
    @Transactional
    public Follow followUser(int userId, int followerId) {
        if(isFollow(userId, followerId)) {
            return null;
        }

        return followRepository.save(new Follow(userId, followerId));
    }

    @Transactional
    public boolean unfollowUser(int userId, int followerId) {
        Follow follow = followRepository.findByUserIdAndFollowerId(userId, followerId);

        if(follow == null) {
            return false;
        }

        followRepository.delete(follow);

        return  true;
    }

    public List<UserInfo> listFollower(int userId) {
        return followRepository.findFollowersByUserId(userId);
    }

    public List<UserInfo> listFollowing(int userId) {
        return followRepository.findFollowingByUserId(userId);
    }
}
