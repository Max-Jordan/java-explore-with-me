package ru.practicum.comments.dto;

import lombok.*;
import ru.practicum.comments.model.CommentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private CommentStatus status;
    private LocalDateTime created;
    private Long author;
    private Long event;
}
