package com.techstore.service;


import com.techstore.dto.reponse.BrandResponse;
import com.techstore.dto.request.BrandRequest;
import com.techstore.entity.Brand;
import com.techstore.enums.BrandStatus;
import com.techstore.mapper.BrandMapper;
import com.techstore.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper ;

    public BrandResponse createBrand(BrandRequest request) {
        if(brandRepository.existsByName(request.getName())){
            throw new RuntimeException("Brand đã tồn tại");
        }
        Brand brand = brandMapper.toBrand(request);
        brandRepository.save(brand);
        return brandMapper.toResponse(brand);

    }
    public BrandResponse getById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Brand"));
        return brandMapper.toResponse(brand) ;
    }
    public List<BrandResponse> getAllBrand(){
        return brandRepository.findAll()
                .stream()
                .map(brandMapper::toResponse)
                .toList();
    }
    public BrandResponse activeBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thay sản phẩm"));
        brand.setStatus(BrandStatus.ACTIVE);
        brandRepository.save(brand);
        return brandMapper.toResponse(brand);

    }

    public BrandResponse blockBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thay sản phẩm"));
        brand.setStatus(BrandStatus.BLOCK);
        brandRepository.save(brand);
        return brandMapper.toResponse(brand);

    }

    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thay sản phẩm"));
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }


}
