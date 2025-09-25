package com.example.steam.module.order.repository;

import com.example.steam.module.order.domain.OrderProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    Page<OrderProduct> findAllByOrderId(Long ordersId, Pageable pageable);
}
