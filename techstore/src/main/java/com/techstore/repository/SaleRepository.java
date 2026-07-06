package com.techstore.repository;

import com.techstore.entity.Sale;
import com.techstore.enums.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    boolean existsByName(String name);
    List<Sale> findByStatus(SaleStatus status) ;
    List<Sale> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(SaleStatus status, LocalDateTime startdate,LocalDateTime endDate);
}
