package com.myfund.advice.controller;

import com.myfund.advice.model.UserProfile;
import com.myfund.advice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 用户画像管理 API。
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserProfileService profileService;

    public ProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<UserProfile> save(@Valid @RequestBody UserProfile profile) {
        return ResponseEntity.ok(profileService.save(profile));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> findByUserId(@PathVariable String userId) {
        Optional<UserProfile> profile = profileService.findByUserId(userId);
        return profile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
