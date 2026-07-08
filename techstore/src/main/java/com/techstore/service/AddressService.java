package com.techstore.service;


import com.techstore.dto.reponse.AddressResponse;
import com.techstore.dto.request.AddressRequest;
import com.techstore.entity.Address;
import com.techstore.entity.User;
import com.techstore.mapper.AddressMapper;
import com.techstore.repository.AddressRepository;
import com.techstore.repository.UserRepository;
import com.techstore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository ;

    public List<AddressResponse> getMyAddresses(CustomUserDetails userDetails) {
        return addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userDetails.getId())
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    public AddressResponse getMyAddressById(CustomUserDetails userDetails, Long id) {
        Address address = addressRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        return addressMapper.toResponse(address);
    }


    @Transactional
    public AddressResponse createAddress(CustomUserDetails userDetails, AddressRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        boolean hasNoAddress = addressRepository
                .findByUserIdOrderByDefaultAddressDescCreatedAtDesc(user.getId())
                .isEmpty();

        if (hasNoAddress || Boolean.TRUE.equals(request.getDefaultAddress())) {
            clearDefaultAddress(user.getId());
            address.setDefaultAddress(true);
        }

        addressRepository.save(address);

        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(
            CustomUserDetails userDetails,
            Long id,
            AddressRequest request
    ) {
        Address address = addressRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setAddressDetail(request.getAddressDetail());

        if (Boolean.TRUE.equals(request.getDefaultAddress())) {
            clearDefaultAddress(userDetails.getId());
            address.setDefaultAddress(true);
        }

        addressRepository.save(address);

        return addressMapper.toResponse(address);
    }

    @Transactional
    public AddressResponse setDefaultAddress(CustomUserDetails userDetails, Long id) {
        Address address = addressRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        clearDefaultAddress(userDetails.getId());

        address.setDefaultAddress(true);
        addressRepository.save(address);

        return addressMapper.toResponse(address);
    }

    @Transactional
    public void deleteAddress(CustomUserDetails userDetails, Long id) {
        Address address = addressRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        boolean wasDefault = Boolean.TRUE.equals(address.getDefaultAddress());

        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remainingAddresses =
                    addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(userDetails.getId());

            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setDefaultAddress(true);
                addressRepository.save(newDefault);
            }
        }
    }

    private void clearDefaultAddress(Long userId) {
        addressRepository.findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(address -> {
                    address.setDefaultAddress(false);
                    addressRepository.save(address);
                });
    }

}
