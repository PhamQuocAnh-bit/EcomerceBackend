package com.techstore.service;

import com.techstore.dto.reponse.PaymentResponse;
import com.techstore.entity.Order;
import com.techstore.entity.Payment;
import com.techstore.enums.PaymentMethod;
import com.techstore.enums.PaymentStatus;
import com.techstore.mapper.PaymentMapper;
import com.techstore.repository.PaymentRepository;
import com.techstore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public Payment createPaymentForOrder(Order order) {
        Payment existingPayment = paymentRepository
                .findByOrderId(order.getId())
                .orElse(null);

        if (existingPayment != null) {
            return existingPayment;
        }

        PaymentStatus initialStatus =
                order.getPaymentMethod() == PaymentMethod.COD
                        ? PaymentStatus.UNPAID
                        : PaymentStatus.UNPAID;

        Payment payment = Payment.builder()
                .paymentCode(generatePaymentCode())
                .order(order)
                .paymentMethod(order.getPaymentMethod())
                .status(initialStatus)
                .amount(order.getTotalAmount())
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public void markCodAsPaid(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy thông tin thanh toán"
                        )
                );

        if (payment.getPaymentMethod() != PaymentMethod.COD) {
            throw new RuntimeException(
                    "Thanh toán này không sử dụng COD"
            );
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new RuntimeException(
                    "Thanh toán đã bị hủy"
            );
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionCode(
                "COD-" + payment.getPaymentCode()
        );
        payment.setPaidAt(LocalDateTime.now());
        payment.setFailureReason(null);

        paymentRepository.save(payment);
    }

    @Transactional
    public void cancelPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElse(null);

        if (payment == null) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException(
                    "Không thể hủy thanh toán đã thành công"
            );
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException(
                    "Thanh toán đã được hoàn tiền"
            );
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setPaymentUrl(null);
        payment.setFailureReason(
                "Đơn hàng đã bị hủy"
        );

        paymentRepository.save(payment);
    }

    @Transactional
    public PaymentResponse getPaymentByOrderId(
            CustomUserDetails user,
            Long orderId
    ) {
        Payment payment = paymentRepository
                .findByOrderIdWithOrderAndUser(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy thanh toán"
                        )
                );

        validateOwner(user, payment);

        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse getPayment(
            CustomUserDetails user,
            Long paymentId
    ) {
        Payment payment = paymentRepository
                .findByIdWithOrderAndUser(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy thanh toán"
                        )
                );

        validateOwner(user, payment);

        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    private void validateOwner(
            CustomUserDetails user,
            Payment payment
    ) {
        Long ownerId = payment
                .getOrder()
                .getUser()
                .getId();

        if (!ownerId.equals(user.getId())) {
            throw new RuntimeException(
                    "Bạn không có quyền truy cập thanh toán này"
            );
        }
    }

    private String generatePaymentCode() {
        String code;

        do {
            code = "PAY-"
                    + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();
        } while (paymentRepository.existsByPaymentCode(code));

        return code;
    }
}