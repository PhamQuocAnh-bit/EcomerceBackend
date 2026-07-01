package com.techstore.service;


import com.techstore.dto.reponse.CategoryReponse;
import com.techstore.dto.request.CategoryRequest;
import com.techstore.entity.Category;
import com.techstore.enums.CategoryStatus;
import com.techstore.mapper.CategoryMapper;
import com.techstore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import  java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryReponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Danh mục đã tồn tại");
        }
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return categoryMapper.toReponse(category);

    }
    public List<CategoryReponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toReponse)
                .toList();
    }

    public CategoryReponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
        return categoryMapper.toReponse(category);
    }

    public CategoryReponse blockCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        category.setStatus(CategoryStatus.BLOCKED);
        categoryRepository.save(category);
        return categoryMapper.toReponse(category);
    }
    public CategoryReponse activeCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        category.setStatus(CategoryStatus.ACTIVE);
        categoryRepository.save(category);
        return categoryMapper.toReponse(category);
    }

    public CategoryReponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        return  categoryMapper.toReponse(category);
    }


}
