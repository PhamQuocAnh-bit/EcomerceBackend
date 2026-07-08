package com.techstore.controller;


import com.techstore.dto.reponse.AddressResponse;
import com.techstore.dto.request.AddressRequest;
import com.techstore.entity.Address;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class AddressController {
    private final AddressService addressService ;

    @GetMapping
    public List<AddressResponse> getMyAddress (@AuthenticationPrincipal CustomUserDetails userDetails) {
        return  addressService.getMyAddresses(userDetails);
    }
    @GetMapping("/{id}")
    public AddressResponse getMyAddressById(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long id){
        return addressService.getMyAddressById(userDetails, id);
    }
    @PostMapping
    public AddressResponse createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressRequest request
    ) {
        return addressService.createAddress(userDetails, request);
    }
    @PutMapping("/{id}")
    public AddressResponse updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request
    ) {
        return addressService.updateAddress(userDetails, id, request);
    }

    @PutMapping("/{id}/default")
    public AddressResponse setDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        return addressService.setDefaultAddress(userDetails, id);
    }

    @DeleteMapping("/{id}")
    public String deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        addressService.deleteAddress(userDetails, id);
        return "Xóa địa chỉ thành công";
    }
}
