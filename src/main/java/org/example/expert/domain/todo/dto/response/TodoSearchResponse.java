package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TodoSearchResponse {

    private final String title;
    private final Integer managersCount;
    private final Integer commentsCount;

    @QueryProjection
    public TodoSearchResponse(String title, Integer managersCount, Integer commentsCount) {
        this.title = title;
        this.managersCount = managersCount;
        this.commentsCount = commentsCount;
    }

}
