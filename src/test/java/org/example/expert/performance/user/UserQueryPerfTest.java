package org.example.expert.performance.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StopWatch;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

@Disabled
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserQueryPerfTest {

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

    @DisplayName("3. (COMPLEX) Projection")
    @Test
    void simpleQuerydsl() {
        long t = measure(() ->
                userRepository.fetchSimple(PREFIX, LIMIT, OFFSET)
        );
        results.put("3. (COMPLEX) Projection", t);
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
