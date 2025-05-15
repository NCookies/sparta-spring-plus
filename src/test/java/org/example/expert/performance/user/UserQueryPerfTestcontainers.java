package org.example.expert.performance.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.expert.domain.user.dto.response.SimpleUserDto;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Disabled
@ActiveProfiles("testcontainers")
@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserQueryPerfTestcontainers {

    @Container
    private static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>("mysql:8.0")
                    .withUsername("test")
                    .withPassword("test")
                    .withDatabaseName("testdb")
                    // mysql 서버에 local-infile 허용
                    .withCommand("--local-infile=1")
                    // JDBC URL 에도 allowLoadLocalInfile=true 추가
                    .withUrlParam("allowLoadLocalInfile", "true")
//                    .withCopyFileToContainer(
//                            MountableFile.forClasspathResource("db/init/users.csv"),
//                            "/docker-entrypoint-initdb.d/users.csv"
//                    )
                    // init.sql 스크립트를 /docker-entrypoint-initdb.d/ 에 자동 실행
                    .withInitScript("db/init/users_init.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    @Autowired private EntityManager em;
    @Autowired private EntityManagerFactory emf;

    // 결과 저장용
    private static final Map<String,Long> results = new LinkedHashMap<>();

    protected final String sampleNickname = "nick-51cffe52-632d-48eb-b1d4-2da86755f2dc";
    private static final String PREFIX = "nick-";
    private final int LIMIT = 20;
    private final int OFFSET = 0;

    @BeforeEach
    void clearAllCaches() {
        // 1차 캐시
        em.clear();
        // JPA 2차 캐시
        emf.getCache().evictAll();
        // Hibernate 쿼리 캐시
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        sessionFactory.getCache().evictQueryRegions();
    }

    private long measure(Runnable action) {
        // 캐시 초기화 한 번 더 보장
        clearAllCaches();
        StopWatch sw = new StopWatch();
        sw.start();
        action.run();
        sw.stop();
        return sw.getTotalTimeMillis();
    }

    @DisplayName("1. (COMPLEX) JPQL")
    @Test
    void simpleJpql() {
        long t = measure(() ->
                userRepository.findSimpleByNicknamePrefix(PREFIX, PageRequest.of(OFFSET, LIMIT))
        );
        results.put("1. (COMPLEX) JPQL", t);
    }

    @DisplayName("2. (COMPLEX) Native")
    @Test
    void simpleNative() {
        long t = measure(() ->
                userRepository.findSimpleNative(PREFIX, LIMIT, OFFSET)
        );
        results.put("2. (COMPLEX) Native", t);
    }

    @DisplayName("3. (COMPLEX) Querydsl")
    @Test
    void simpleQuerydsl() {
        long t = measure(() ->
                userRepository.fetchSimple(PREFIX, LIMIT, OFFSET)
        );
        results.put("3. (COMPLEX) Querydsl", t);
    }

    @DisplayName("4. (SIMPLE) Basic JPA")
    @Test
    void basicJpa() {
        long t = measure(() ->
                userService.findUserByNickname(sampleNickname)
        );
        results.put("4. (SIMPLE) Basic JPA", t);
    }

    @DisplayName("5. (SIMPLE) Native")
    @Test
    void serviceNative() {
        long t = measure(() ->
                userService.findUserByNicknameNativeQuery(sampleNickname)
        );
        results.put("5. (SIMPLE) Native", t);
    }

    @DisplayName("6. (SIMPLE) Projection")
    @Test
    void serviceProjection() {
        long t = measure(() ->
                userService.findUserByNicknameProjection(sampleNickname)
        );
        results.put("6. (SIMPLE) Projection", t);
    }

    @Test
    void test() {
        Optional<User> byId = userRepository.findById(1L);
        Page<SimpleUserDto> simpleByNicknamePrefix = userRepository.findSimpleByNicknamePrefix(PREFIX, PageRequest.of(OFFSET, LIMIT));
        UserResponse userByNickname = userService.findUserByNickname(sampleNickname);
        UserResponse userByNicknameNativeQuery = userService.findUserByNicknameNativeQuery(sampleNickname);
        UserResponse userByNicknameProjection = userService.findUserByNicknameProjection(sampleNickname);
        System.out.println(userByNicknameProjection.getId());
        System.out.println(userByNicknameProjection.getEmail());
    }

    @AfterAll
    static void printSummary() {
        System.out.println("\n=== Performance Summary ===");
        System.out.printf("%-25s %10s%n", "Method", "Time (ms)");
        System.out.println("------------------------- ----------");
        results.entrySet().stream()
                // key 앞 숫자(1,2,3...) 기준으로 정렬
                .sorted(Comparator.comparingInt(e -> {
                    String key = e.getKey();
                    return Integer.parseInt(key.substring(0, key.indexOf('.')));
                }))
                .forEach(e ->
                        System.out.printf("%-25s %10d%n", e.getKey(), e.getValue())
                );
    }

}
