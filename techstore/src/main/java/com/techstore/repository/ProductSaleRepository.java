package com.techstore.repository;

import com.techstore.entity.ProductSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductSaleRepository extends JpaRepository<ProductSale,Long> {
    List<ProductSale> findBySaleId(Long saleId);
    List<ProductSale> findByProductId(Long productId) ;
    void deleteBySaleId(Long saleId) ;
}
