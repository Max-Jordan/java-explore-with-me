package ru.practicum.compilations;

import java.util.List;

public interface CompilationsService {

    CompilationDto save(NewCompilationDto dtp);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
