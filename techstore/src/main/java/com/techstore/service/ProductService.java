package com.techstore.service;

import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.dto.reponse.ProductResponse;
import com.techstore.dto.request.ProductImageRequest;
import com.techstore.dto.request.ProductRequest;
import com.techstore.entity.*;
import com.techstore.enums.BrandStatus;
import com.techstore.enums.CategoryStatus;
import com.techstore.enums.ProductStatus;
import com.techstore.mapper.ProductMapper;
import com.techstore.repository.*;
import com.techstore.utils.SlugUntil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;
    private final SlugUntil slugUntil;
    private final CloudinaryService cloudinaryService;

    private final ProductSaleRepository productSaleRepository;
    private final ProductPricingService productPricingService;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = buildProduct(request);
        productRepository.save(product);

        return toProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return toProductResponses(products);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        return toProductResponse(product);
    }

    public List<ProductResponse> getAvailableProducts() {
        List<Product> products = productRepository.findAvailableProducts(
                ProductStatus.ACTIVE,
                CategoryStatus.ACTIVE,
                BrandStatus.ACTIVE
        );

        return toProductResponses(products);
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findAvailableProductsByCategory(
                categoryId,
                ProductStatus.ACTIVE,
                CategoryStatus.ACTIVE,
                BrandStatus.ACTIVE
        );

        return toProductResponses(products);
    }

    public List<ProductResponse> getProductsByBrand(Long brandId) {
        List<Product> products = productRepository.findAvailableProductsByBrand(
                brandId,
                ProductStatus.ACTIVE,
                CategoryStatus.ACTIVE,
                BrandStatus.ACTIVE
        );

        return toProductResponses(products);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Sản phẩm đã tồn tại");
        }

        String slug = slugUntil.generateSlug(request.getName());

        if (!product.getSlug().equals(slug)
                && productRepository.existsBySlug(slug)) {
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

        productRepository.save(product);

        return toProductResponse(product);
    }

    public ProductResponse activeProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);

        return toProductResponse(product);
    }

    public ProductResponse blockProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setStatus(ProductStatus.BLOCK);
        productRepository.save(product);

        return toProductResponse(product);
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

        Product product = buildProduct(request);

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

        return toProductResponse(product);
    }

    private Product buildProduct(ProductRequest request) {
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

        return Product.builder()
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
    }

    private ProductResponse toProductResponse(Product product) {
        List<ProductSale> activeSales =
                productSaleRepository.findActiveSalesByProductIds(List.of(product.getId()));

        ProductPriceResult price =
                productPricingService.calculatePrice(product, activeSales);

        return productMapper.toResponse(product, price);
    }

    private List<ProductResponse> toProductResponses(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        List<ProductSale> activeSales =
                productSaleRepository.findActiveSalesByProductIds(productIds);

        Map<Long, List<ProductSale>> saleMap = activeSales.stream()
                .collect(Collectors.groupingBy(ps -> ps.getProduct().getId()));

        return products.stream()
                .map(product -> {
                    List<ProductSale> salesOfProduct =
                            saleMap.getOrDefault(product.getId(), List.of());

                    ProductPriceResult price =
                            productPricingService.calculatePrice(product, salesOfProduct);

                    return productMapper.toResponse(product, price);
                })
                .toList();
    }
}