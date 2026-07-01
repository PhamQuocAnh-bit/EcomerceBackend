package com.techstore.controller;


import com.techstore.dto.reponse.ProductImageResponse;
import com.techstore.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
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
}
