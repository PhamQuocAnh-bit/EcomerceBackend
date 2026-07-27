package com.techstore.repository;

import com.techstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import com.techstore.dto.reponse.TopProductResponse;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    boolean existsByOrderCode(String orderCode);

    boolean existsByUserIdAndStatusAndOrderDetailsProductId(Long userId, com.techstore.enums.OrderStatus status, Long productId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status <> com.techstore.enums.OrderStatus.CANCELLED")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT new com.techstore.dto.reponse.TopProductResponse(d.product.id, d.product.name, d.product.sku, SUM(d.quantity), SUM(d.totalPrice)) " +
           "FROM Order o JOIN o.orderDetails d " +
           "WHERE o.status <> com.techstore.enums.OrderStatus.CANCELLED " +
           "GROUP BY d.product.id, d.product.name, d.product.sku " +
           "ORDER BY SUM(d.quantity) DESC")
    List<TopProductResponse> findTopSellingProducts(Pageable pageable);
}


