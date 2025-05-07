package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TodoSearchRequest {

    private final String weather;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private final LocalDate endDate;

}
