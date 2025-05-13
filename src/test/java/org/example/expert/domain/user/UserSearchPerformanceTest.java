package org.example.expert.domain.user;

import org.example.expert.domain.user.repository.UserRepository;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("testcontainers")
@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserSearchPerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final String sampleNickname = "nick-1824fdcf-fea0-43ce-b236-4ce072335a4c";

    @Container
    static MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>("mysql:8.0.33")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test")
                    .withInitScript("db/init/users_init.sql");

    // 스프링이 컨텍스트를 만들면서 내장(embedded) DB 설정을 판단하는 시점이 Testcontainers JUnit 확장보다 앞서기 때문에 에러가 발생한다.
    // 이를 방지하기 위해 테이너를 프로퍼티 주입 전에 static 블럭에서 수동으로 시작시킨다.
    static {
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url",      MYSQL_CONTAINER::getJdbcUrl);
        reg.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        reg.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        reg.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
    }

    @DisplayName("검색 성능 테스트")
    @Test
    void measure() {
        StopWatch sw = new StopWatch();
        sw.start();
        userService.findUserByNickname(sampleNickname);
        sw.stop();
        System.out.println("검색 시간 : " + sw.getTotalTimeMillis() + " ms");
    }

}
