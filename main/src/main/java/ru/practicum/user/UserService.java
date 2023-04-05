package ru.practicum.user;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto saveUser(UserDto dto);

    List<UserDto> findUsers(List<Long> id, Integer from, Integer size);

    void deleteUser(Long id);

    User getUserById(Long userId);
}
