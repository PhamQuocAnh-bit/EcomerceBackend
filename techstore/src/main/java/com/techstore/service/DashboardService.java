package com.techstore.service;

import com.techstore.dto.reponse.DashboardStatsResponse;
import com.techstore.dto.reponse.TopProductResponse;
import com.techstore.repository.OrderRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DashboardStatsResponse getDashboardStats() {
        Long totalUsers = userRepository.count();
        Long totalProducts = productRepository.count();
        Long totalOrders = orderRepository.count();
        java.math.BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        List<TopProductResponse> topSellingProducts = orderRepository.findTopSellingProducts(PageRequest.of(0, 10));

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .topSellingProducts(topSellingProducts)
                .build();
    }
}
