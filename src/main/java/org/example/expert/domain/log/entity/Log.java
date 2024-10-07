package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private final LocalDateTime createdAt = LocalDateTime.now();

    private Log(String message) {
        this.message = message;
    }

    public static Log from(String message) {
        return new Log(message);
    }
}
