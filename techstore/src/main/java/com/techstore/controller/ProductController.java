package com.techstore.controller;


import com.techstore.dto.reponse.ProductResponse;
import com.techstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAvailableProducts();

    }
    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }
    @GetMapping("/brand/{brandId}")
    public List<ProductResponse> getProductsByBrand(@PathVariable Long brandId) {
        return productService.getProductsByBrand(brandId);
    }

//    @GetMapping("/slug/{slug}")
//    public ProductResponse getProductBySlug(@PathVariable String slug) {
//        return productService.getProductBySlug(slug);
//        }

}
