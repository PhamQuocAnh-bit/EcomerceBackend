package com.techstore.service;

import com.techstore.dto.reponse.ProductImageResponse;
import com.techstore.entity.Product;
import com.techstore.entity.ProductImage;
import com.techstore.mapper.ProductMapper;
import com.techstore.repository.ProductImageRepository;
import com.techstore.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;

    @Transactional
    public List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        int currentCount = productImageRepository.findByProductIdOrderBySortOrderAsc(productId).size();

        for (int i = 0; i < files.size(); i++) {
            String imageUrl = cloudinaryService.uploadImage(files.get(i));

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .sortOrder(currentCount + i + 1)
                    .mainImage(currentCount == 0 && i == 0)
                    .build();

            productImageRepository.save(image);
        }

        return productImageRepository.findByProductIdOrderBySortOrderAsc(productId)
                .stream()
                .map(productMapper::toImageResponse)
                .toList();
    }


}
