package org.example.expert.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.profile.dto.response.ProfileImageResponse;
import org.example.expert.domain.profile.service.ProfileImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileImageService profileImageService;

    @PostMapping("/image")
    public ResponseEntity<ProfileImageResponse> uploadProfileImage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam MultipartFile file
    ) {

        ProfileImageResponse res = profileImageService.upload(authUser.getId(), file);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/image")
    public ResponseEntity<ProfileImageResponse> getProfileImage(@RequestParam Long userId) {

        ProfileImageResponse res = profileImageService.get(userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal AuthUser authUser
    ) {

        profileImageService.delete(authUser.getId());
        return ResponseEntity.ok().build();
    }

}
