package ru.practicum.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationsMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CompilationsMapper.makeCompilationDto;
import static ru.practicum.mapper.PaginationMapper.makePageable;

@RequiredArgsConstructor
@Service
public class CompilationsServiceImpl implements CompilationsService {

    private final CompilationsRepository repository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto dto) {
        List<Event> events = new ArrayList<>();
        if (!dto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(dto.getEvents());
        }
        Set<Event> eventSet = new HashSet<>(events);
        Compilation compilation = CompilationsMapper.makeCompilation(dto);
        compilation.setEvents(eventSet);
        return makeCompilationDto(repository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        checkCompilation(compId);
        repository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = checkCompilation(compId);
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        return makeCompilationDto(repository.saveAndFlush(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return repository.findAllByPinned(pinned, makePageable(from, size)).stream()
                .map(CompilationsMapper::makeCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return makeCompilationDto(checkCompilation(compId));
    }

    private Compilation checkCompilation(Long compId) {
        return repository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with id: " + compId + "was not found"));
    }
}
