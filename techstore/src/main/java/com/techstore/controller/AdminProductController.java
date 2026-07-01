package com.techstore.controller;


import com.techstore.dto.reponse.ProductResponse;
import com.techstore.dto.request.ProductRequest;
import com.techstore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
//    @PostMapping
//    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
//        return productService.createProduct(request);
//    }
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @PutMapping("/{id}/active")
    public ProductResponse activeProduct(@PathVariable Long id) {
        return productService.activeProduct(id);
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse createProduct(
            @ModelAttribute @Valid ProductRequest request,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return productService.createProductWithImages(request, files);
    }

    @PutMapping("/{id}/block")
    public ProductResponse blockProduct(@PathVariable Long id) {
        return productService.blockProduct(id);
    }


}
