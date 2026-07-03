package com.techstore.service;

import com.techstore.dto.reponse.ProductImageResponse;
import com.techstore.dto.reponse.ProductResponse;
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

    @Transactional
    public List<ProductImageResponse> setMainImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        ProductImage selectedImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh"));
        if(!selectedImage.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Hình ảnh không thuộc sản phẩm này");
        }
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        for (ProductImage image : images) {
            image.setMainImage(false);
        }
        selectedImage.setMainImage(true);
        productImageRepository.saveAll(images);
        return productImageRepository.findByProductIdOrderBySortOrderAsc(productId)
                .stream()
                .map(productMapper::toImageResponse)
                .toList();

    }

    @Transactional
    public List<ProductImageResponse> deleteImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh"));
        if(!image.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Hình ảnh không thuộc sản phẩm này");}
        boolean wasMainImage = Boolean.TRUE.equals(image.getMainImage());
        productImageRepository.delete(image);
        List<ProductImage> remainingImages =
                productImageRepository.findByProductIdOrderBySortOrderAsc(productId);

        for (int i = 0; i < remainingImages.size(); i++) {
            ProductImage img = remainingImages.get(i);
            img.setSortOrder(i + 1);

            if (wasMainImage && i == 0) {
                img.setMainImage(true);
            }
        }
        productImageRepository.saveAll(remainingImages);

        return remainingImages.stream()
                .map(productMapper::toImageResponse)
                .toList();
    }

}
