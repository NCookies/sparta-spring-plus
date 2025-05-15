package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.dto.response.SimpleUserDto;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.util.List;

public interface UserRepositoryQuery {

    UserResponse findByNicknameProjection(String nickname);

    List<SimpleUserDto> fetchSimple(
            String prefix, int limit, int offset
    );

}
