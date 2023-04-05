package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public UserDto addUser(@Valid @RequestBody UserDto dto) {
        log.info("Was received a request to save user {}", dto);
        return service.saveUser(dto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam List<Long> id,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Was received a request to find users with params: id={}, from={}, size={}", id, from, size);
        return service.findUsers(id, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        log.info("Was received a request to delete user with id {}", userId);
        service.deleteUser(userId);
    }
}
