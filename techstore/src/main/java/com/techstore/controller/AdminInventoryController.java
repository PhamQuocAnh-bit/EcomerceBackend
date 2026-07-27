package com.techstore.controller;

import com.techstore.dto.reponse.InventoryTransactionResponse;
import com.techstore.dto.request.InventoryTransactionRequest;
import com.techstore.service.InventoryTransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AdminInventoryController {

    private final InventoryTransactionService inventoryService;

    @PostMapping("/import")
    public InventoryTransactionResponse importStock(@Valid @RequestBody InventoryTransactionRequest request) {
        return inventoryService.manualImport(request);
    }

    @PostMapping("/export")
    public InventoryTransactionResponse exportStock(@Valid @RequestBody InventoryTransactionRequest request) {
        return inventoryService.manualExport(request);
    }

    @GetMapping("/history")
    public List<InventoryTransactionResponse> getHistory() {
        return inventoryService.getAllHistory();
    }

    @GetMapping("/history/product/{productId}")
    public List<InventoryTransactionResponse> getProductHistory(@PathVariable Long productId) {
        return inventoryService.getHistoryByProduct(productId);
    }
}
