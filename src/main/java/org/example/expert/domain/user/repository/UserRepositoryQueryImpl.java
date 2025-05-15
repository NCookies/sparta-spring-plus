package org.example.expert.domain.user.repository;

import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.response.QUserResponse;
import org.example.expert.domain.user.dto.response.SimpleUserDto;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.QUser;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryQueryImpl implements UserRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public UserResponse findByNicknameProjection(String nickname) {

        return queryFactory
                .select(new QUserResponse(
                        user.id,
                        user.email
                ))
                .from(user)
                .where(user.nickname.eq(nickname))
                .fetchOne();
    }

    @Override
    public List<SimpleUserDto> fetchSimple(
            String prefix, int limit, int offset
    ) {
        QUser u = QUser.user;

        // CASE 식
        var category = new CaseBuilder()
                .when(u.userRole.eq(UserRole.ADMIN))
                .then("AdminUser")
                .otherwise("NormalUser");

        return queryFactory
                .select(Projections.constructor(
                        SimpleUserDto.class,
                        u.id,
                        u.email,
                        u.nickname,
                        u.userRole,
                        // 서브쿼리: 같은 역할의 사용자 수
                        com.querydsl.jpa.JPAExpressions
                                .select(u.count())
                                .from(u)
                                .where(u.userRole.eq(u.userRole)),
                        // CASE
                        category
                ))
                .from(u)
                .where(u.nickname.startsWith(prefix))
                .orderBy(u.createdAt.desc())
                .limit(limit)
                .offset(offset)
                .fetch();
    }

}
