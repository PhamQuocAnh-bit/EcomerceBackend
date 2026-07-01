package com.techstore.controller;


import com.techstore.dto.reponse.CategoryReponse;
import com.techstore.dto.request.CategoryRequest;
import com.techstore.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryReponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryReponse getCategoryById(@PathVariable  Long id) {
        return categoryService.getById(id);
    }

    @PostMapping("")
    public CategoryReponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PostMapping("/{id}")
    public CategoryReponse updateCategory(@PathVariable Long id, @RequestBody
                                          @Valid CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @PutMapping("/{id}/active")
    public CategoryReponse activeCategory(@PathVariable Long id) {
        return categoryService.activeCategory(id);
    }

    @PutMapping("/{id}/block")
    public CategoryReponse blockCategory(@PathVariable Long id) {
        return categoryService.blockCategory(id);
    }
}
//    @DeleteMapping("/{id}")
//    public CategoryReponse deleteCategory(@PathVariable Long id) {
//        categoryService.
//    }
//}
