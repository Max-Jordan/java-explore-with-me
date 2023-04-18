package ru.practicum.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.CommentStatus;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.exception.CommentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CommentMapper.makeComment;
import static ru.practicum.mapper.CommentMapper.makeCommentDto;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public CommentDto save(Long userId, Long eventId, CommentDto dto) {
        Comment comment = makeComment(dto);
        User author = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        comment.setStatus(CommentStatus.PENDING);
        comment.setEvent(event);
        comment.setAuthor(author);
        return makeCommentDto(repository.save(comment));
    }

    @Override
    public CommentDto updateAvailable(boolean available, Long commentId) {
        Comment comment = getComment(commentId);
        comment.setStatus(available ? CommentStatus.PUBLISHED : CommentStatus.REJECTED);
        return makeCommentDto(repository.saveAndFlush(comment));
    }

    @Override
    public void deleteCommentForAdmin(Long commentId) {
        repository.deleteById(commentId);
    }

    @Override
    public CommentDto findById(Long userId, Long commentId) {
        userService.getUserById(userId);
        return makeCommentDto(getComment(commentId));
    }

    @Override
    public CommentDto updateById(Long userId, Long commentId, UpdateCommentDto dto) {
        userService.getUserById(userId);
        Comment comment = getComment(commentId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new CommentException("This comment belong to another user");
        }
        comment.setText(dto.getText());
        return makeCommentDto(repository.saveAndFlush(comment));
    }

    @Override
    public Comment getComment(Long commentId) {
        return repository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment with id " + commentId +
                " was not found"));
    }

    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId) {
        return repository.findCommentsByEventId(eventId).stream()
                .map(CommentMapper::makeCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = getComment(commentId);
        if (!Objects.equals(comment.getAuthor().getId(), userService.getUserById(userId).getId())) {
            throw new CommentException("This comment belong to another user");
        }
        repository.deleteById(commentId);
    }
}
