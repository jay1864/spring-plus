package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.TodoProjection;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TodoQueryRepository {
    Optional<Todo> findByIdByDsl(long todoId);
    // 메서드 네이밍이 이게 최선인지 다시 고민!!
    Page<TodoProjection> findAllBySearch(Pageable pageable, String title, String nickname, LocalDate startDate, LocalDate endedDate);
}
