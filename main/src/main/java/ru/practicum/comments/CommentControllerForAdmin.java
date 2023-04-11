package ru.practicum.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comment/{commentId}")
public class CommentControllerForAdmin {

    private final CommentService service;

    @PatchMapping
    public CommentDto updateAvailable(@PathVariable Long commentId,
                                      @RequestParam boolean available) {
        log.info("Received a request to update status comment {} to status {} confirmed(true)/rejected(false)"
                , commentId, available);
        return service.updateAvailable(available, commentId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentForAdmin(@PathVariable Long commentId) {
        log.info("Received a request to delete comment {} from admin", commentId);
        service.deleteCommentForAdmin(commentId);
    }
}