package com.techstore.repository;

import com.techstore.enums.BrandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techstore.entity.Brand;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existsByName(String name) ;
    List<Brand> findByStatus(BrandStatus status) ;



}
