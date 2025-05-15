package org.example.expert.domain.user.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;

@Getter
public class SimpleUserDto {
    private Long id;
    private String email;
    private String nickname;
    private UserRole userRole;
    private Long roleCount;
    private String category;

    @QueryProjection
    public SimpleUserDto(Long id, String email, String nickname,
                         UserRole userRole, Long roleCount, String category) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.userRole = userRole;
        this.roleCount = roleCount;
        this.category = category;
    }

}
