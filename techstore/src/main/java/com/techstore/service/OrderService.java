package com.techstore.service;

import com.techstore.dto.reponse.OrderResponse;
import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.dto.request.CreateOrderRequest;
import com.techstore.dto.request.UpdateOrderStatusRequest;
import com.techstore.entity.*;
import com.techstore.enums.OrderStatus;
import com.techstore.enums.PaymentMethod;
import com.techstore.enums.ProductStatus;
import com.techstore.mapper.OrderMapper;
import com.techstore.repository.*;
import com.techstore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal SHIPPING_FEE = BigDecimal.ZERO;

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ProductSaleRepository productSaleRepository;
    private final ProductPricingService productPricingService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(CustomUserDetails userDetails, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng đang trống"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));

        if (!address.getUser().getId().equals(userDetails.getId())) {
            throw new RuntimeException("Địa chỉ không thuộc tài khoản của bạn");
        }

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .user(cart.getUser())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .addressDetail(address.getAddressDetail())
                .paymentMethod(request.getPaymentMethod())
                .status(resolveInitialStatus(request.getPaymentMethod()))
                .shippingFee(SHIPPING_FEE)
                .note(request.getNote())
                .build();

        BigDecimal totalProductAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            validateProductBeforeOrder(product, cartItem.getQuantity());

            ProductPriceResult price = productPricingService.calculatePrice(
                    product,
                    productSaleRepository.findActiveSalesByProductId(product.getId())
            );

            BigDecimal totalPrice = price.getFinalPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .productImage(getMainImage(product))
                    .originalPrice(price.getOriginalPrice())
                    .salePrice(price.getSalePrice())
                    .finalPrice(price.getFinalPrice())
                    .onSale(price.getOnSale())
                    .discountAmount(price.getDiscountAmount())
                    .discountType(price.getDiscountType())
                    .discountValue(price.getDiscountValue())
                    .saleName(price.getSaleName())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(totalPrice)
                    .build();

            order.getOrderDetails().add(orderDetail);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setSoldQuantity(product.getSoldQuantity() + cartItem.getQuantity());

            productRepository.save(product);

            totalProductAmount = totalProductAmount.add(totalPrice);
        }

        order.setTotalProductAmount(totalProductAmount);
        order.setTotalAmount(totalProductAmount.add(SHIPPING_FEE));

        Order savedOrder = orderRepository.save(order);

        // Cart có orphanRemoval = true nên clear là đủ
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders(CustomUserDetails userDetails) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userDetails.getId())
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getMyOrderById(CustomUserDetails userDetails, Long id) {
        Order order = orderRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse getOrderByIdForAdmin(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        validateStatusTransition(oldStatus, newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelMyOrder(CustomUserDetails userDetails, Long id) {
        Order order = orderRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ xác nhận");
        }

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    private void validateProductBeforeOrder(Product product, Integer quantity) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new RuntimeException("Sản phẩm " + product.getName() + " hiện không còn bán");
        }

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ số lượng tồn kho");
        }
    }

    private void validateStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == newStatus) {
            return;
        }

        if (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.DELIVERED) {
            throw new RuntimeException("Không thể cập nhật đơn hàng đã hoàn tất hoặc đã hủy");
        }

        boolean valid =
                oldStatus == OrderStatus.PENDING && (
                        newStatus == OrderStatus.CONFIRMED ||
                                newStatus == OrderStatus.CANCELLED
                )
                        ||
                        oldStatus == OrderStatus.CONFIRMED && (
                                newStatus == OrderStatus.SHIPPING ||
                                        newStatus == OrderStatus.CANCELLED
                        )
                        ||
                        oldStatus == OrderStatus.SHIPPING &&
                                newStatus == OrderStatus.DELIVERED;

        if (!valid) {
            throw new RuntimeException("Không thể chuyển trạng thái từ " + oldStatus + " sang " + newStatus);
        }
    }

    private void restoreStock(Order order) {
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();

            product.setStockQuantity(product.getStockQuantity() + detail.getQuantity());
            product.setSoldQuantity(Math.max(0, product.getSoldQuantity() - detail.getQuantity()));

            productRepository.save(product);
        }
    }

    private OrderStatus resolveInitialStatus(PaymentMethod paymentMethod) {
        if (paymentMethod == PaymentMethod.COD) {
            return OrderStatus.PENDING;
        }

        // Sau này làm VNPay/Momo thì online payment nên có PaymentStatus riêng.
        return OrderStatus.PENDING;
    }

    private String generateOrderCode() {
        String code;

        do {
            code = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (orderRepository.existsByOrderCode(code));

        return code;
    }

    private String getMainImage(Product product) {
        if (product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }

        return product.getImages()
                .stream()
                .filter(image -> Boolean.TRUE.equals(image.getMainImage()))
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(product.getImages().get(0).getImageUrl());
    }
}