package ru.practicum.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class PrivateCommentController {

    private final CommentService service;

    @PostMapping("/{userId}/{eventId}/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto save(@PathVariable Long userId,
                           @PathVariable Long eventId,
                           @RequestBody @Valid CommentDto dto) {
        log.info("Received a request to post comment {} from user {} to event {}", dto, userId, eventId);
        return service.save(userId, eventId, dto);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto updateById(@PathVariable Long userId,
                                 @PathVariable Long commentId,
                                 @RequestBody @Valid UpdateCommentDto dto) {
        log.info("Received a request to update comment {}, from user {}, new comment {} ", commentId, userId, dto);
        return service.updateById(userId, commentId, dto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        log.info("Received a request to delete comment {} from user {} ", commentId, userId);
        service.deleteCommentByUser(userId, commentId);
    }

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentDto findById(@PathVariable Long userId,
                               @PathVariable Long commentId) {
        log.info("Received a request to get comment by id {} from user {}", commentId, userId);
        return service.findById(userId, commentId);
    }

    @GetMapping("/event/{eventId}/comments")
    public List<CommentDto> getAllCommentsByEvent(@PathVariable Long eventId) {
        log.info("Received a request to get all comments by event with id {}", eventId);
        return service.getAllCommentsByEvent(eventId);
    }
}
