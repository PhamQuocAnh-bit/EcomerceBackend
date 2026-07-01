package com.techstore.mapper;


import com.techstore.dto.reponse.CategoryReponse;
import com.techstore.dto.request.CategoryRequest;
import com.techstore.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
    public CategoryReponse toReponse(Category category) {
        return CategoryReponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

}
