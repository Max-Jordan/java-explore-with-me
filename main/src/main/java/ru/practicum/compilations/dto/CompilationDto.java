package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDto {
    private List<EventShortDto> events;
    private long id;
    private boolean pinned;
    @NotBlank
    @Size(max = 200)
    private String title;

}