package com.techstore.repository;

import com.techstore.entity.Payment;
import com.techstore.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    boolean existsByPaymentCode(String paymentCode);

    boolean existsByTransactionCode(String transactionCode);

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByPaymentCode(String paymentCode);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("""
        SELECT p
        FROM Payment p
        JOIN FETCH p.order o
        JOIN FETCH o.user u
        WHERE p.id = :paymentId
        """)
    Optional<Payment> findByIdWithOrderAndUser(
            @Param("paymentId") Long paymentId
    );

    @Query("""
        SELECT p
        FROM Payment p
        JOIN FETCH p.order o
        JOIN FETCH o.user u
        WHERE o.id = :orderId
        """)
    Optional<Payment> findByOrderIdWithOrderAndUser(
            @Param("orderId") Long orderId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Payment p
        JOIN FETCH p.order o
        WHERE p.paymentCode = :paymentCode
        """)
    Optional<Payment> findByPaymentCodeForUpdate(
            @Param("paymentCode") String paymentCode
    );
}