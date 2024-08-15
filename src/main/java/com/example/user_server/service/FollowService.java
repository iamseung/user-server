package com.example.user_server.service;

import com.example.user_server.dto.FollowMessage;
import com.example.user_server.dto.UserInfo;
import com.example.user_server.entity.Follow;
import com.example.user_server.repository.FollowRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {
    private FollowRepository followRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public FollowService(FollowRepository followRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.followRepository = followRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
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
        sendFollowerMessage(userId, followerId, true);
        return followRepository.save(new Follow(userId, followerId));
    }

    @Transactional
    public boolean unfollowUser(int userId, int followerId) {
        Follow follow = followRepository.findByUserIdAndFollowerId(userId, followerId);

        if(follow == null) {
            return false;
        }

        sendFollowerMessage(userId, followerId, false);
        followRepository.delete(follow);

        return true;
    }

    // isFollow -> 팔로우 여부
    private void sendFollowerMessage(int userId, int followerId, boolean isFollow) {
        FollowMessage message = new FollowMessage(userId, followerId, isFollow);
        try {
            kafkaTemplate.send("user.follower", objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserInfo> listFollower(int userId) {
        return followRepository.findFollowersByUserId(userId);
    }

    public List<UserInfo> listFollowing(int userId) {
        return followRepository.findFollowingByUserId(userId);
    }
}
