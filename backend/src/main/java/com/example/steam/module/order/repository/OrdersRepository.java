package com.example.steam.module.order.repository;

import com.example.steam.module.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Page<Orders> findAllByMemberIdOrderByCreatedDate(Long memberId, Pageable pageable);
}
