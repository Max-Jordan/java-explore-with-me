package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryControllerForAdmin {

    private final CategoryService service;

    @PostMapping
    public CategoryDto saveCategory(@Valid @RequestBody CategoryDto dto) {
        log.info("Received a request for admin to save category {}", dto);
        return service.saveCategory(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Positive @PathVariable Long catId,
            @Valid @RequestBody CategoryDto dto) {
        log.info("Received a request to update category: id = {}, category = {}", catId, dto);
        return service.updateCategory(dto, catId);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@Positive @PathVariable Long catId) {
        log.info("Received a request to delete category with id {}", catId);
        service.deleteCategory(catId);
    }
}
