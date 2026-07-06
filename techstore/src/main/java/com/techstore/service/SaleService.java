package com.techstore.service;

import com.techstore.dto.reponse.SaleResponse;
import com.techstore.dto.request.SaleRequest;
import com.techstore.entity.Product;
import com.techstore.entity.ProductSale;
import com.techstore.entity.Sale;
import com.techstore.enums.SaleStatus;
import com.techstore.mapper.SaleMapper;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProductSaleRepository;
import com.techstore.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ProductSaleRepository productSaleRepository;
    private final SaleMapper saleMapper;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        if (saleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên chương trình sale đã tồn tại");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        Sale sale = Sale.builder()
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(SaleStatus.ACTIVE)
                .build();

        saleRepository.save(sale);

        if (request.getProductIds() != null) {
            for (Long productId : request.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id = " + productId));
                ProductSale productSale = ProductSale.builder()
                        .product(product)
                        .sale(sale)
                        .build();
                productSaleRepository.save(productSale);

            }

        }
        List<ProductSale> productSales = productSaleRepository.findBySaleId(sale.getId());

        return saleMapper.toResponse(sale, productSales);
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll()
                .stream()
                .map(sale -> saleMapper.toResponse(
                        sale,
                        productSaleRepository.findBySaleId(sale.getId())
                ))
                .toList();
    }

    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sale"));

        return saleMapper.toResponse(
                sale,
                productSaleRepository.findBySaleId(id)
        );
    }
    @Transactional
    public SaleResponse updateSale(Long id, SaleRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sale"));

        if (!sale.getName().equals(request.getName())
                && saleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên chương trình sale đã tồn tại");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        sale.setName(request.getName());
        sale.setDescription(request.getDescription());
        sale.setDiscountType(request.getDiscountType());
        sale.setDiscountValue(request.getDiscountValue());
        sale.setStartDate(request.getStartDate());
        sale.setEndDate(request.getEndDate());

        productSaleRepository.deleteBySaleId(id);

        if (request.getProductIds() != null) {
            for (Long productId : request.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id = " + productId));

                productSaleRepository.save(
                        ProductSale.builder()
                                .product(product)
                                .sale(sale)
                                .build()
                );
            }
        }

        saleRepository.save(sale);

        return saleMapper.toResponse(
                sale,
                productSaleRepository.findBySaleId(id)
        );
    }
    public SaleResponse activeSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sale"));

        sale.setStatus(SaleStatus.ACTIVE);
        saleRepository.save(sale);

        return saleMapper.toResponse(
                sale,
                productSaleRepository.findBySaleId(id)
        );
    }
    public SaleResponse blockSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sale"));

        sale.setStatus(SaleStatus.BLOCK);
        saleRepository.save(sale);

        return saleMapper.toResponse(
                sale,
                productSaleRepository.findBySaleId(id)
        );
    }

}