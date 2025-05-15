package org.example.expert.domain.profile.repository;

import org.example.expert.domain.profile.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    Optional<ProfileImage> findByUser_Id(Long userId);
    
}
