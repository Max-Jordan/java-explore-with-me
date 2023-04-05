package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.CategoryMapper.makeCategory;
import static ru.practicum.mapper.CategoryMapper.makeCategoryDto;
import static ru.practicum.mapper.PaginationMapper.makePageable;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository repository;
    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return repository.findAll(makePageable(from, size)).stream()
                .map(CategoryMapper::makeCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return makeCategoryDto(repository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id " + catId +
                " was not found")));
    }

    @Override
    public CategoryDto saveCategory(CategoryDto dto) {
        return makeCategoryDto(repository.save(makeCategory(dto)));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto dto, Long catId) {
        dto.setId(catId);
        return makeCategoryDto(repository.saveAndFlush(makeCategory(dto)));
    }

    @Override
    public void deleteCategory(Long catId) {
        repository.delete(makeCategory(getCategory(catId)));
    }
}
