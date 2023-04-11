package ru.practicum.compilations;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationsService {

    CompilationDto save(NewCompilationDto dtp);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
