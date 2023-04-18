package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment makeComment(CommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setStatus(dto.getStatus());
        comment.setId(dto.getId());
        comment.setCreated(dto.getCreated());
        return comment;
    }

    public static CommentDto makeCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setEvent(comment.getEvent().getId());
        dto.setAuthor(comment.getAuthor().getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setStatus(comment.getStatus());
        dto.setId(comment.getId());
        return dto;
    }
}
