package org.example.expert.domain.common.dto;

import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private final Long id;
    private final String email;
    private final UserRole userRole;

    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    // 유저는 권한 한 가지만 가지고 있음
    public Collection<GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> "ROLE_" + userRole.name());
    }

}
