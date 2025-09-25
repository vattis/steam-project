package com.example.steam.module.shoppingCart.repository;

import com.example.steam.module.shoppingCart.domain.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

}
