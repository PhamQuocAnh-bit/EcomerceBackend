package com.techstore.controller;


import com.techstore.dto.reponse.SaleResponse;
import com.techstore.dto.request.SaleRequest;
import com.techstore.service.SaleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/sales")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminSaleController {
    private final SaleService saleService ;

    @GetMapping
    public List<SaleResponse> getAllSale() {
        return saleService.getAllSales();
    }


    @GetMapping("/{id}")
    public SaleResponse getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }

    @PostMapping
    public SaleResponse createSale(@Valid @RequestBody SaleRequest request) {
        return saleService.createSale(request);
    }

    @PutMapping("/{id}")
    public SaleResponse updateSale(
            @PathVariable Long id,
            @Valid @RequestBody SaleRequest request
    ) {
        return saleService.updateSale(id, request);
    }

    @PutMapping("/{id}/active")
    public SaleResponse activeSale(@PathVariable Long id) {
        return saleService.activeSale(id);
    }

    @PutMapping("/{id}/block")
    public SaleResponse blockSale(@PathVariable Long id) {
        return saleService.blockSale(id);
    }
}
