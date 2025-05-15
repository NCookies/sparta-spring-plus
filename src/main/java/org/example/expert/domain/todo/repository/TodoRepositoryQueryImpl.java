package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchCondRequest;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<TodoSearchResponse> findAllBySearchCond(TodoSearchCondRequest searchCond, Pageable pageable) {

        BooleanBuilder booleanBuilder = buildTodoSearchPredicate(searchCond);

        JPAQuery<TodoSearchResponse> query = queryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        todo.managers.size(),
                        todo.comments.size()
                ))
                .from(todo)
                .where(booleanBuilder)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        JPAQuery<Long> countQuery = queryFactory
                .select(Wildcard.count)
                .from(todo)
                .where(booleanBuilder);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    private BooleanBuilder buildTodoSearchPredicate(TodoSearchCondRequest searchCond) {

        return new BooleanBuilder()
                .and(titleContains(searchCond.getTitle()))
                .and(createdAfter(searchCond.getStartDateTime()))
                .and(createdBefore(searchCond.getEndDateTime()))
                .and(nicknameContains(searchCond.getNickname()));
    }

    private BooleanExpression titleContains(String title) {
        return Objects.nonNull(title) ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression createdAfter(LocalDateTime start) {
        return Objects.nonNull(start)
                ? todo.createdAt.goe(start)
                : null;
    }

    private BooleanExpression createdBefore(LocalDateTime end) {
        return Objects.nonNull(end)
                ? todo.createdAt.loe(end)
                : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return Objects.nonNull(nickname) ? todo.managers.any().user.nickname.containsIgnoreCase(nickname) : null;
    }

}
