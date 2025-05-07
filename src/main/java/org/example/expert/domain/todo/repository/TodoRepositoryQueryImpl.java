package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

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

}
