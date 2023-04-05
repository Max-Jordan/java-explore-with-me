package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.compilations.CompilationDto;
import ru.practicum.compilations.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationsMapper {

    public static Compilation makeCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.isPinned());
        return compilation;
    }

    public static CompilationDto makeCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setEvents(compilation.getEvents().stream()
                .map(EventMapper::makeShortDto)
                .collect(Collectors.toList()));
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        return dto;
    }
}
