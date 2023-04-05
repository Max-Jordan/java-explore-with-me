package ru.practicum.category;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);

    CategoryDto saveCategory(CategoryDto dto);

    CategoryDto updateCategory(CategoryDto dto, Long catId);

    void deleteCategory(Long catId);
}
