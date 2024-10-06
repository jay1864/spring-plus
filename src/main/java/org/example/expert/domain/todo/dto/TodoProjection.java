package org.example.expert.domain.todo.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TodoProjection {

    private final String title;
    private final Long managerCount;
    private final Long commentCount;

    @QueryProjection
    public TodoProjection(String title, Long managerCount, Long commentCount) {
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
