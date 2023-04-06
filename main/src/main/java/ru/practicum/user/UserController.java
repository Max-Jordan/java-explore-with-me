package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody UserDto dto) {
        log.info("Was received a request to save user {}", dto);
        return service.saveUser(dto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
                                  @Positive @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Was received a request to find users with params: id={}, from={}, size={}", ids, from, size);
        return service.findUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Was received a request to delete user with id {}", userId);
        service.deleteUser(userId);
    }
}
