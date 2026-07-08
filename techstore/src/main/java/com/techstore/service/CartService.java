package com.techstore.service;


import com.techstore.dto.reponse.CartResponse;
import com.techstore.dto.request.AddToCartRequest;
import com.techstore.dto.request.UpdateCartItemRequest;
import com.techstore.entity.Cart;
import com.techstore.entity.CartItem;
import com.techstore.entity.Product;
import com.techstore.entity.User;
import com.techstore.enums.ProductStatus;
import com.techstore.mapper.CartMapper;
import com.techstore.repository.CartItemRepository;
import com.techstore.repository.CartRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.UserRepository;
import com.techstore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository ;
    private final CartItemRepository cartItemRepository ;
    private final ProductRepository productRepository ;
    private final UserRepository userRepository ;
    private final CartMapper cartMapper ;

    @Transactional
    public CartResponse clearCart(CustomUserDetails userDetails) {
        Cart cart = getOrCreateCart(userDetails.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(CustomUserDetails userDetails, Long itemId) {
        Cart cart = getOrCreateCart(userDetails.getId());

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item không thuộc giỏ hàng của bạn");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(
            CustomUserDetails userDetails,
            Long itemId,
            UpdateCartItemRequest request
    ) {
        Cart cart = getOrCreateCart(userDetails.getId());

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item không thuộc giỏ hàng của bạn");
        }

        validateProduct(item.getProduct(), request.getQuantity());

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(CustomUserDetails userDetails, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userDetails.getId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        validateProduct(product, request.getQuantity());

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        } else {
            int newQuantity = item.getQuantity() + request.getQuantity();
            validateProduct(product, newQuantity);
            item.setQuantity(newQuantity);
        }

        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    public CartResponse getMyCart(CustomUserDetails userDetails) {
        Cart cart = getOrCreateCart(userDetails.getId());
        return cartMapper.toResponse(cart);
    }


    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    private void validateProduct(Product product , Integer quantity) {
        if(product.getStatus() != ProductStatus.ACTIVE) {
            throw  new RuntimeException("Sản phầm hiện tại không được bán");
        }
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ");
        }
    }



}
