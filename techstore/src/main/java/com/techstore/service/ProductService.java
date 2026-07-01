package com.techstore.service;


import com.techstore.dto.reponse.ProductResponse;
import com.techstore.dto.request.ProductImageRequest;
import com.techstore.dto.request.ProductRequest;
import com.techstore.entity.Brand;
import com.techstore.entity.Category;
import com.techstore.entity.Product;
import com.techstore.entity.ProductImage;
import com.techstore.enums.BrandStatus;
import com.techstore.enums.CategoryStatus;
import com.techstore.enums.ProductStatus;
import com.techstore.mapper.ProductMapper;
import com.techstore.repository.BrandRepository;
import com.techstore.repository.CategoryRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.utils.SlugUntil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final SlugUntil slugUntil;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if(productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Sản phẩm đã tồn tại");
        }
        String slug = slugUntil.generateSlug(request.getName());
        if(productRepository.existsBySlug(slug)) {
            throw new RuntimeException("Slug đã tồn tại");}
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu"));
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .originalPrice(request.getOriginalPrice())
                .stockQuantity(request.getStockQuantity())
                .soldQuantity(0)
                .status(ProductStatus.ACTIVE)
                .category(category)
                .brand(brand)
                .build();
      //  addProductImage(product, request.getImages());
        productRepository.save(product);
        return  productMapper.toResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        return productMapper.toResponse(product);
    }
    public List<ProductResponse> getAvailableProducts() {
        return productRepository.findAvailableProducts(
                        ProductStatus.ACTIVE,
                        CategoryStatus.ACTIVE,
                        BrandStatus.ACTIVE
                ).stream()
                .map(productMapper::toResponse)
                .toList();
    }
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findAvailableProductsByCategory(
                        categoryId,
                        ProductStatus.ACTIVE,
                        CategoryStatus.ACTIVE,
                        BrandStatus.ACTIVE
                ).stream()
                .map(productMapper::toResponse)
                .toList();
    }
    public List<ProductResponse> getProductsByBrand(Long brandId) {
        return productRepository.findAvailableProductsByBrand(
                        brandId,
                        ProductStatus.ACTIVE,
                        CategoryStatus.ACTIVE,
                        BrandStatus.ACTIVE
                ).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public void addProductImage(Product product, List<ProductImageRequest> images) {
        if(images == null || images.isEmpty()) {
            throw new RuntimeException("Danh sách hình ảnh không được để trống");
        }
        for(int i=0; i<images.size(); i++) {
            ProductImageRequest image = images.get(i);

            ProductImage productImage = ProductImage.builder()
                    .imageUrl(image.getImageUrl())
                    .mainImage(i==0)
                    .sortOrder(i+1)
                    .product(product)
                    .build();
            product.getImages().add(productImage);
        }

    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        if(!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Sản phẩm đã tồn tại");
        }
        String slug = slugUntil.generateSlug(request.getName());
        if(!product.getSlug().equals(slug) && productRepository.existsBySlug(slug)) {
            throw new RuntimeException("Slug đã tồn tại");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu"));
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setBrand(brand);
//        product.getImages().clear();
//        addProductImage(product, request.getImages());
        productRepository.save(product);
        return  productMapper.toResponse(product);
    }

    public ProductResponse activeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
        return  productMapper.toResponse(product);
    }

    public ProductResponse blockProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        product.setStatus(ProductStatus.BLOCK);
        productRepository.save(product);
        return  productMapper.toResponse(product);
    }
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse createProductWithImages(ProductRequest request, List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("Danh sách hình ảnh không được để trống");
        }

        if (productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Sản phẩm đã tồn tại");
        }

        String slug = slugUntil.generateSlug(request.getName());

        if (productRepository.existsBySlug(slug)) {
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu"));

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .originalPrice(request.getOriginalPrice())
                .stockQuantity(request.getStockQuantity())
                .soldQuantity(0)
                .status(ProductStatus.ACTIVE)
                .category(category)
                .brand(brand)
                .build();

        for (int i = 0; i < files.size(); i++) {
            String imageUrl = cloudinaryService.uploadImage(files.get(i));

            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .mainImage(i == 0)
                    .sortOrder(i + 1)
                    .build();

            product.getImages().add(image);
        }

        productRepository.save(product);

        return productMapper.toResponse(product);
    }



//    public String generateSlug(String input) {
//        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
//        String slug = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
//                .matcher(normalized)
//                .replaceAll("");
//        return slug.toLowerCase()
//                .replaceAll("[^a-z0-9\\s-]", "")
//                .replaceAll("\\s+", "-")
//                .replaceAll("-{2,}", "-")
//                .replaceAll("^-|-$", "")
//                .trim();
//    }
}
