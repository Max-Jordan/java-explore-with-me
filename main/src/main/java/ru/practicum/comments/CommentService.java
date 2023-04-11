package ru.practicum.comments;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDto save(Long userId, Long eventId, CommentDto dto);

    CommentDto updateAvailable(boolean available, Long commentId);

    void deleteCommentForAdmin(Long commentId);

    CommentDto findById(Long userId, Long commentId);

    CommentDto updateById(Long userId, Long commentId, UpdateCommentDto dto);

    Comment getComment(Long commentId);

    List<CommentDto> getAllCommentsByEvent(Long eventId);

    void deleteCommentByUser(Long userId, Long commentId);
}
