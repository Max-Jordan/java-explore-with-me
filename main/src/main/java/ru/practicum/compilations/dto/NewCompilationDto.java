package ru.practicum.compilations.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Value
public class NewCompilationDto {
    private List<Long> events;
    private boolean pinned;
    @NotBlank
    @Size(max = 200)
    private String title;
}
