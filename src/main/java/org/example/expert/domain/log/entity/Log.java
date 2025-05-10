package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private LogType type;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Log(String message, LogType type) {
        this.message = message;
        this.type = type;
    }
}
