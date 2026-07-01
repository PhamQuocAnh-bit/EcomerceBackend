package com.techstore.repository;

import com.techstore.entity.Product;
import com.techstore.enums.BrandStatus;
import com.techstore.enums.CategoryStatus;
import com.techstore.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    boolean existsBySlug(String slug);
    Optional<Product> findBySlug(String slug);
    List<Product> getProductByStatus(ProductStatus status) ;
    List<Product> findByCategoryIdAndStatus(Long categoryId, ProductStatus status);
    List<Product> findByBrandIdAndStatus(Long brandId, ProductStatus status);
    @Query("""
SELECT p FROM Product p 
WHERE p.status = :productStatus
AND p.category.status = :categoryStatus
AND p.brand.status = :brandStatus
""")
    List<Product> findAvailableProducts(
            ProductStatus productStatus,
            CategoryStatus categoryStatus,
            BrandStatus brandStatus
    );
    @Query("""
SELECT p FROM Product p
WHERE p.brand.id = :brandId
AND p.status = :productStatus
AND p.category.status = :categoryStatus
AND p.brand.status = :brandStatus
""")
    List<Product> findAvailableProductsByBrand(
            Long brandId,
            ProductStatus productStatus,
            CategoryStatus categoryStatus,
            BrandStatus brandStatus
    );
    @Query("""
SELECT p FROM Product p
WHERE p.category.id = :categoryId
AND p.status = :productStatus
AND p.category.status = :categoryStatus
AND p.brand.status = :brandStatus
""")
    List<Product> findAvailableProductsByCategory(
            Long categoryId,
            ProductStatus productStatus,
            CategoryStatus categoryStatus,
            BrandStatus brandStatus
    );



}
