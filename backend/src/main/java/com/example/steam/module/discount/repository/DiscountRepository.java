package com.example.steam.module.discount.repository;

import com.example.steam.module.discount.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
