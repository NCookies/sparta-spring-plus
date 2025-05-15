package org.example.expert.domain.profile.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.profile.dto.response.ProfileImageResponse;
import org.example.expert.domain.profile.entity.ProfileImage;
import org.example.expert.domain.profile.repository.ProfileImageRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

    private static final String DIR = "profile-images";

    private final S3Service s3Service;

    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProfileImageResponse upload(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        String url = s3Service.upload(file, DIR);
        String key = url.substring(url.indexOf(DIR));

        ProfileImage profileImage = profileImageRepository.findByUser_Id(user.getId())
                .map(image -> {
                    s3Service.delete(image.getObjectKey());
                    image.updateObjectKey(key);
                    return image;
                })
                .orElseGet(() -> ProfileImage.builder()
                        .objectKey(key)
                        .user(user)
                        .build());

        ProfileImage saved = profileImageRepository.save(profileImage);

        return ProfileImageResponse.from(s3Service.getPublicUrl(saved.getObjectKey()));
    }

    @Transactional(readOnly = true)
    public ProfileImageResponse get(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        ProfileImage profileImage = profileImageRepository.findByUser_Id(user.getId()).orElse(null);

        if (profileImage == null) {
            return ProfileImageResponse.from(null);
        }
        return ProfileImageResponse.from(s3Service.getPublicUrl(profileImage.getObjectKey()));
    }

    @Transactional
    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        ProfileImage profileImage = profileImageRepository.findByUser_Id(user.getId()).orElse(null);

        if (profileImage != null) {
            s3Service.delete(profileImage.getObjectKey());
            profileImageRepository.delete(profileImage);
        }
    }

}
