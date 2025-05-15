package org.example.expert.domain.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageResponse {

    private final String url;

    public static ProfileImageResponse from(String url) {
        return new ProfileImageResponse(url);
    }

}
