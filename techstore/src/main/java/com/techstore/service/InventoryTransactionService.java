package com.techstore.service;

import com.techstore.dto.reponse.InventoryTransactionResponse;
import com.techstore.dto.request.InventoryTransactionRequest;
import com.techstore.entity.InventoryTransaction;
import com.techstore.entity.Product;
import com.techstore.enums.InventoryTransactionType;
import com.techstore.mapper.InventoryTransactionMapper;
import com.techstore.repository.InventoryTransactionRepository;
import com.techstore.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionMapper transactionMapper;

    @Transactional
    public InventoryTransactionResponse manualImport(InventoryTransactionRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setStockQuantity(product.getStockQuantity() + request.getQuantity());
        productRepository.save(product);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .type(InventoryTransactionType.IMPORT)
                .quantity(request.getQuantity())
                .referenceCode(request.getReferenceCode())
                .note(request.getNote() != null ? request.getNote() : "Nhập kho thủ công")
                .build();

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public InventoryTransactionResponse manualExport(InventoryTransactionRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ để xuất kho (" 
                    + product.getStockQuantity() + " < " + request.getQuantity() + ")");
        }

        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        productRepository.save(product);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .type(InventoryTransactionType.EXPORT)
                .quantity(request.getQuantity())
                .referenceCode(request.getReferenceCode())
                .note(request.getNote() != null ? request.getNote() : "Xuất kho thủ công")
                .build();

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void recordOrderExport(Product product, Integer quantity, String orderCode) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .type(InventoryTransactionType.EXPORT)
                .quantity(quantity)
                .referenceCode(orderCode)
                .note("Xuất kho tự động cho đơn hàng: " + orderCode)
                .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public void recordOrderRestore(Product product, Integer quantity, String orderCode) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .type(InventoryTransactionType.IMPORT)
                .quantity(quantity)
                .referenceCode(orderCode)
                .note("Hoàn kho tự động từ đơn hàng hủy: " + orderCode)
                .build();
        transactionRepository.save(transaction);
    }

    public List<InventoryTransactionResponse> getAllHistory() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    public List<InventoryTransactionResponse> getHistoryByProduct(Long productId) {
        return transactionRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(transactionMapper::toResponse)
                .toList();
    }
}
