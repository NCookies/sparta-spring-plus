package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.dto.response.SimpleUserDto;
import org.example.expert.domain.user.dto.response.SimpleUserProjection;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    @Query(value = "SELECT * FROM users WHERE nickname = :nickname", nativeQuery = true)
    Optional<User> findByNicknameNativeQuery(@Param("nickname") String nickname);

    boolean existsByEmail(String email);

    @Query(value = ""
            + "SELECT new org.example.expert.domain.user.dto.response.SimpleUserDto("
            + "  u.id, u.email, u.nickname, u.userRole, "
            + "  (SELECT COUNT(u2) FROM User u2 WHERE u2.userRole = u.userRole), "
            + "  CASE WHEN u.userRole = org.example.expert.domain.user.enums.UserRole.ADMIN THEN 'AdminUser' ELSE 'NormalUser' END"
            + ") "
            + "FROM User u "
            + "WHERE u.nickname LIKE CONCAT(:prefix, '%') "
            + "ORDER BY u.createdAt DESC",
            countQuery = "SELECT COUNT(u) FROM User u WHERE u.nickname LIKE CONCAT(:prefix, '%')"
    )
    Page<SimpleUserDto> findSimpleByNicknamePrefix(
            @Param("prefix") String prefix,
            Pageable pageable
    );

    @Query(value = """
        SELECT
          u.id,
          u.email,
          u.nickname,
          u.user_role AS userRole,
          (SELECT COUNT(*) FROM users u2 WHERE u2.user_role = u.user_role) AS roleCount,
          CASE
            WHEN u.user_role = 'ADMIN' THEN 'AdminUser'
            ELSE 'NormalUser'
          END AS category
        FROM users u
        WHERE u.nickname LIKE CONCAT(:prefix, '%')
        ORDER BY u.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<SimpleUserProjection> findSimpleNative(
            @Param("prefix") String prefix,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

}
