package com.techstore.service;

import com.techstore.config.VNPayProperties;
import com.techstore.dto.reponse.PaymentResponse;
import com.techstore.dto.reponse.VNPayCallbackResponse;
import com.techstore.dto.reponse.VNPayIpnResponse;
import com.techstore.entity.Payment;
import com.techstore.enums.PaymentMethod;
import com.techstore.enums.PaymentStatus;
import com.techstore.mapper.PaymentMapper;
import com.techstore.repository.PaymentRepository;
import com.techstore.security.CustomUserDetails;
import com.techstore.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VNPayProperties properties;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponse createPaymentUrl(
            CustomUserDetails user,
            Long orderId,
            HttpServletRequest request
    ) {
        Payment payment = paymentRepository
                .findByOrderIdWithOrderAndUser(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy thông tin thanh toán"
                        )
                );

        validateOwner(user, payment);
        validatePaymentCanStart(payment);

        ZoneId zoneId = ZoneId.of(properties.getTimeZone());
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime expireTime = now.plusMinutes(
                properties.getExpireMinutes()
        );

        Map<String, String> parameters = new HashMap<>();

        parameters.put(
                "vnp_Version",
                properties.getVersion()
        );
        parameters.put(
                "vnp_Command",
                properties.getCommand()
        );
        parameters.put(
                "vnp_TmnCode",
                properties.getTmnCode()
        );
        parameters.put(
                "vnp_Amount",
                toVNPayAmount(payment.getAmount())
        );
        parameters.put(
                "vnp_CurrCode",
                properties.getCurrencyCode()
        );
        parameters.put(
                "vnp_TxnRef",
                payment.getPaymentCode()
        );
        parameters.put(
                "vnp_OrderInfo",
                "Thanh toan don hang "
                        + payment.getOrder().getOrderCode()
        );
        parameters.put(
                "vnp_OrderType",
                properties.getOrderType()
        );
        parameters.put(
                "vnp_Locale",
                properties.getLocale()
        );
        parameters.put(
                "vnp_ReturnUrl",
                properties.getReturnUrl()
        );
        parameters.put(
                "vnp_IpAddr",
                VNPayUtil.getClientIp(request)
        );
        parameters.put(
                "vnp_CreateDate",
                now.format(VNPAY_DATE_FORMAT)
        );
        parameters.put(
                "vnp_ExpireDate",
                expireTime.format(VNPAY_DATE_FORMAT)
        );

        String hashData =
                VNPayUtil.buildHashData(parameters);

        String secureHash =
                VNPayUtil.hmacSHA512(
                        properties.getHashSecret(),
                        hashData
                );

        String paymentUrl =
                properties.getPayUrl()
                        + "?"
                        + VNPayUtil.buildQueryString(parameters)
                        + "&vnp_SecureHash="
                        + secureHash;

        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentUrl(paymentUrl);
        payment.setExpiredAt(
                expireTime.toLocalDateTime()
        );
        payment.setFailureReason(null);

        Payment savedPayment =
                paymentRepository.save(payment);

        return paymentMapper.toResponse(savedPayment);
    }

    @Transactional
    public VNPayCallbackResponse processReturn(
            HttpServletRequest request
    ) {
        Map<String, String> parameters =
                VNPayUtil.getCallbackParameters(request);

        CallbackResult result =
                processCallback(parameters);

        return VNPayCallbackResponse.builder()
                .success(result.success())
                .message(result.message())
                .paymentId(result.payment().getId())
                .orderId(result.payment().getOrder().getId())
                .paymentCode(result.payment().getPaymentCode())
                .orderCode(
                        result.payment().getOrder().getOrderCode()
                )
                .paymentStatus(result.payment().getStatus())
                .transactionCode(
                        result.payment().getTransactionCode()
                )
                .responseCode(
                        result.payment().getResponseCode()
                )
                .transactionStatus(
                        result.payment().getTransactionStatus()
                )
                .build();
    }

    @Transactional
    public VNPayIpnResponse processIpn(
            HttpServletRequest request
    ) {
        Map<String, String> parameters =
                VNPayUtil.getCallbackParameters(request);

        String paymentCode = parameters.get("vnp_TxnRef");

        if (!VNPayUtil.verifySignature(
                parameters,
                properties.getHashSecret()
        )) {
            return new VNPayIpnResponse(
                    "97",
                    "Invalid signature"
            );
        }

        Payment payment = paymentRepository
                .findByPaymentCodeForUpdate(paymentCode)
                .orElse(null);

        if (payment == null) {
            return new VNPayIpnResponse(
                    "01",
                    "Order not found"
            );
        }

        BigDecimal callbackAmount;

        try {
            callbackAmount = parseVNPayAmount(
                    parameters.get("vnp_Amount")
            );
        } catch (RuntimeException exception) {
            return new VNPayIpnResponse(
                    "04",
                    "Invalid amount"
            );
        }

        if (payment.getAmount().compareTo(callbackAmount) != 0) {
            return new VNPayIpnResponse(
                    "04",
                    "Invalid amount"
            );
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return new VNPayIpnResponse(
                    "02",
                    "Order already confirmed"
            );
        }

        updatePaymentFromCallback(payment, parameters);

        paymentRepository.save(payment);

        return new VNPayIpnResponse(
                "00",
                "Confirm success"
        );
    }

    private CallbackResult processCallback(
            Map<String, String> parameters
    ) {
        if (!VNPayUtil.verifySignature(
                parameters,
                properties.getHashSecret()
        )) {
            throw new RuntimeException(
                    "Chữ ký VNPay không hợp lệ"
            );
        }

        String paymentCode = requireParameter(
                parameters,
                "vnp_TxnRef"
        );

        Payment payment = paymentRepository
                .findByPaymentCodeForUpdate(paymentCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy giao dịch"
                        )
                );

        BigDecimal callbackAmount = parseVNPayAmount(
                requireParameter(parameters, "vnp_Amount")
        );

        if (payment.getAmount().compareTo(callbackAmount) != 0) {
            throw new RuntimeException(
                    "Số tiền thanh toán không hợp lệ"
            );
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return new CallbackResult(
                    true,
                    "Giao dịch đã được xác nhận trước đó",
                    payment
            );
        }

        updatePaymentFromCallback(payment, parameters);
        paymentRepository.save(payment);

        boolean success =
                payment.getStatus() == PaymentStatus.PAID;

        return new CallbackResult(
                success,
                success
                        ? "Thanh toán thành công"
                        : getFailureMessage(
                        payment.getResponseCode()
                ),
                payment
        );
    }

    private void updatePaymentFromCallback(
            Payment payment,
            Map<String, String> parameters
    ) {
        String responseCode =
                parameters.get("vnp_ResponseCode");

        String transactionStatus =
                parameters.get("vnp_TransactionStatus");

        String transactionCode =
                parameters.get("vnp_TransactionNo");

        payment.setResponseCode(responseCode);
        payment.setTransactionStatus(transactionStatus);
        payment.setBankCode(parameters.get("vnp_BankCode"));
        payment.setCardType(parameters.get("vnp_CardType"));

        if (transactionCode != null
                && !transactionCode.isBlank()) {
            payment.setTransactionCode(transactionCode);
        }

        boolean success =
                "00".equals(responseCode)
                        && "00".equals(transactionStatus);

        if (success) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            payment.setFailureReason(null);
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(
                getFailureMessage(responseCode)
        );
    }

    private void validatePaymentCanStart(Payment payment) {
        if (payment.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw new RuntimeException(
                    "Đơn hàng không sử dụng phương thức VNPay"
            );
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException(
                    "Đơn hàng đã được thanh toán"
            );
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new RuntimeException(
                    "Thanh toán đã bị hủy"
            );
        }

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException(
                    "Thanh toán đã được hoàn tiền"
            );
        }
    }

    private void validateOwner(
            CustomUserDetails user,
            Payment payment
    ) {
        if (!payment.getOrder()
                .getUser()
                .getId()
                .equals(user.getId())) {
            throw new RuntimeException(
                    "Bạn không có quyền thanh toán đơn hàng này"
            );
        }
    }

    private String toVNPayAmount(BigDecimal amount) {
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Số tiền thanh toán không hợp lệ"
            );
        }

        return amount
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.UNNECESSARY)
                .toPlainString();
    }

    private BigDecimal parseVNPayAmount(
            String vnpAmount
    ) {
        if (vnpAmount == null || vnpAmount.isBlank()) {
            throw new RuntimeException(
                    "VNPay không trả về số tiền"
            );
        }

        try {
            return new BigDecimal(vnpAmount)
                    .divide(
                            BigDecimal.valueOf(100),
                            2,
                            RoundingMode.UNNECESSARY
                    );
        } catch (NumberFormatException exception) {
            throw new RuntimeException(
                    "Số tiền VNPay không hợp lệ"
            );
        }
    }

    private String requireParameter(
            Map<String, String> parameters,
            String name
    ) {
        String value = parameters.get(name);

        if (value == null || value.isBlank()) {
            throw new RuntimeException(
                    "Thiếu tham số VNPay: " + name
            );
        }

        return value;
    }

    private String getFailureMessage(String responseCode) {
        if (responseCode == null) {
            return "Giao dịch không thành công";
        }

        return switch (responseCode) {
            case "00" -> "Giao dịch thành công";
            case "07" ->
                    "Giao dịch bị nghi ngờ gian lận";
            case "09" ->
                    "Thẻ hoặc tài khoản chưa đăng ký Internet Banking";
            case "10" ->
                    "Xác thực thông tin thẻ không đúng quá số lần cho phép";
            case "11" ->
                    "Giao dịch đã hết thời gian thanh toán";
            case "12" ->
                    "Thẻ hoặc tài khoản đã bị khóa";
            case "13" ->
                    "Mã OTP không chính xác";
            case "24" ->
                    "Khách hàng đã hủy giao dịch";
            case "51" ->
                    "Tài khoản không đủ số dư";
            case "65" ->
                    "Tài khoản vượt quá hạn mức giao dịch";
            case "75" ->
                    "Ngân hàng đang bảo trì";
            case "79" ->
                    "Nhập sai mật khẩu thanh toán quá số lần cho phép";
            default ->
                    "Giao dịch không thành công, mã lỗi: "
                            + responseCode;
        };
    }

    private record CallbackResult(
            boolean success,
            String message,
            Payment payment
    ) {
    }
}