package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.QTodoProjection;
import org.example.expert.domain.todo.dto.TodoProjection;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdByDsl(long todoId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(todo)
                        .from(todo)
                        .join(todo.user, user).fetchJoin()
                        .where(todoIdEq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<TodoProjection> findAllBySearch(Pageable pageable, String title, String nickname, LocalDate startDate, LocalDate endedDate) {
        // Todo 관련 데이터 리스트
        List<TodoProjection> projections = jpaQueryFactory
                .select(
                        new QTodoProjection(
                                todo.title,
                                manager.countDistinct(),
                                comment.countDistinct()
                        )
                ).from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where( // Null 일 경우 조건 생략
                        titleContainsOrNull(title),
                        nicknameContainsOrNull(nickname),
                        createdDateBetween(startDate, endedDate)
                )
                .groupBy(todo.id)   // 중복 제거
                .orderBy(todo.createdAt.desc()) // 생성일 기준 내림차순 정렬
                .offset(pageable.getOffset())   // 페이지 시작 위치
                .limit(pageable.getPageSize())  // 페이지 크기(가져올 결과 크기)
                .fetch();

        // 페이지네이션 구현을 위한 데이터 개수
        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(todo)
                .where(
                        titleContainsOrNull(title),
                        nicknameContainsOrNull(nickname),
                        createdDateBetween(startDate, endedDate)
                )
                .fetchOne();

        return new PageImpl<>(projections, pageable, totalCount);
    }

    private BooleanExpression todoIdEq(Long todoId) {
        return todoId != null ? todo.id.eq(todoId) : null;
    }

    private BooleanExpression titleContainsOrNull(String title) {
        return StringUtils.hasText(title) ? todo.title.contains(title) : null;
    }

    private BooleanExpression nicknameContainsOrNull(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickName.contains(nickname) : null;
    }

    private BooleanExpression createdDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return QTodo.todo.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }
        return null;
    }
}
