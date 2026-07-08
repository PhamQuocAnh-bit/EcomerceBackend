package com.techstore.repository;

import com.techstore.entity.ProductSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductSaleRepository extends JpaRepository<ProductSale,Long> {
    List<ProductSale> findBySaleId(Long saleId);
    List<ProductSale> findByProductId(Long productId) ;
    void deleteBySaleId(Long saleId) ;
    @Query("""
        SELECT ps
        FROM ProductSale ps
        JOIN FETCH ps.sale s
        JOIN FETCH ps.product p
        WHERE p.id IN :productIds
        AND s.status = com.techstore.enums.SaleStatus.ACTIVE
        AND s.startDate <= CURRENT_TIMESTAMP
        AND s.endDate >= CURRENT_TIMESTAMP
    """)
    List<ProductSale> findActiveSalesByProductIds(List<Long> productIds);

    @Query("""
    SELECT ps FROM ProductSale ps
    JOIN ps.sale s
    WHERE ps.product.id = :productId
    AND s.status = com.techstore.enums.SaleStatus.ACTIVE
    AND s.startDate <= CURRENT_TIMESTAMP
    AND s.endDate >= CURRENT_TIMESTAMP
""")
    List<ProductSale> findActiveSalesByProductId(Long productId);


}
