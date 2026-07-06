package com.techstore.controller;


import com.techstore.dto.reponse.BrandResponse;
import com.techstore.dto.request.BrandRequest;
import com.techstore.entity.Brand;
import com.techstore.service.BrandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public List<BrandResponse> getAllBrand() {
        return brandService.getAllBrand();
    }
    @GetMapping("/{id}")
    public BrandResponse getById(@PathVariable Long id) {
        return brandService.getById(id);
    }
    @PutMapping("/{id}")
    public BrandResponse updateBrand(@PathVariable Long id, @Valid @RequestBody
                                     BrandRequest request) {
        return brandService.updateBrand(id,request);
    }
    @PostMapping
    public BrandResponse createBrand(@Valid @RequestBody BrandRequest request){
        return brandService.createBrand(request);
    }
    @PutMapping("/{id}/active")
    public BrandResponse activeBrand(@PathVariable Long id) {
        return brandService.activeBrand(id) ;
    }

    @PutMapping("/{id}/block")
    public BrandResponse blockBrand(@PathVariable Long id) {
        return brandService.blockBrand(id);
    }
}
