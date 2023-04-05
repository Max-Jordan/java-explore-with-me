package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.compilations.NewCompilationDto;
import ru.practicum.compilations.model.Compilations;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationsMapper {

    public static Compilations makeCompilation(NewCompilationDto dto) {
        Compilations compilations = new Compilations();
        compilations.setTitle(dto.getTitle());
        compilations.setPinned(dto.isPinned());
        return compilations;
    }

    public static CompilationDto makeCompilationDto(Compilations compilations) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilations.getId());
        dto.setEvents(compilations.getEvents().stream()
                .map(EventMapper::makeShortDto)
                .collect(Collectors.toList()));
        dto.setTitle(compilations.getTitle());
        dto.setPinned(compilations.getPinned());
        return dto;
    }
}
