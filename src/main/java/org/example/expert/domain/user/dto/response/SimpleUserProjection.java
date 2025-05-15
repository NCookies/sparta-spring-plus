package org.example.expert.domain.user.dto.response;

import org.example.expert.domain.user.enums.UserRole;

// 네이티브 쿼리용 Projection 인터페이스
public interface SimpleUserProjection {
    Long getId();
    String getEmail();
    String getNickname();
    UserRole getUserRole();
    Long getRoleCount();
    String getCategory();
}
