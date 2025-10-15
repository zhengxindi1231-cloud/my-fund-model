package com.myfund.advice.service;

import com.myfund.advice.model.UserProfile;
import com.myfund.advice.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户画像管理：提供创建、更新、查询能力。
 */
@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserProfile save(UserProfile profile) {
        return repository.save(profile);
    }

    public Optional<UserProfile> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }
}
