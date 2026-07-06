package com.techstore.controller;


import com.techstore.dto.reponse.ProductImageResponse;
import com.techstore.service.ProductImageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;
    @PostMapping("/{productId}/images")
    public List<ProductImageResponse> uploadImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        return productImageService.uploadImages(productId, files);
    }
    @PutMapping("/{productId}/images/{imageId}/main")
    public List<ProductImageResponse> setMainImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        return productImageService.setMainImage(productId, imageId);
    }
    @DeleteMapping("/{productId}/images/{imageId}")
    public List<ProductImageResponse> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        return productImageService.deleteImage(productId, imageId);
    }
}
