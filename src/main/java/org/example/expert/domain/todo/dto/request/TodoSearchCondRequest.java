package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class TodoSearchCondRequest {

    private final String title;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate end;

    private final String nickname;

    public LocalDateTime getStartDateTime() {
        return Objects.nonNull(start) ? start.atStartOfDay() : null;
    }

    public LocalDateTime getEndDateTime() {
        return Objects.nonNull(end) ? end.atTime(LocalTime.MAX) : null;
    }

}
