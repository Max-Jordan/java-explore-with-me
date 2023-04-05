package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.PaginationMapper.makePageable;
import static ru.practicum.mapper.UserMapper.makeUser;
import static ru.practicum.mapper.UserMapper.makeUserDto;

@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    @Override
    @Transactional
    public UserDto saveUser(UserDto dto) {
        return makeUserDto(repository.save(makeUser(dto)));
    }

    @Override
    public List<UserDto> findUsers(List<Long> id, Integer from, Integer size) {
        return id.isEmpty() ? repository.findAll(makePageable(from,size)).stream()
                .map(UserMapper::makeUserDto)
                .collect(Collectors.toList()) : repository.findAllById(id).stream()
                .map(UserMapper::makeUserDto)
                .collect(Collectors.toList());
    }
    @Override
    public void deleteUser(Long id) {
        repository.delete(repository.findById(id).orElseThrow(() -> new NotFoundException("User with id: " + id +
                " was not found")));
    }

    @Override
    public User getUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new NotFoundException("User with the id " + userId +
                " doesn't exist"));
    }
}
