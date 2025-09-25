package com.example.steam.module.shoppingCart.repository;

import com.example.steam.module.shoppingCart.domain.ShoppingCartProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, Long> {
    Page<ShoppingCartProduct> findAllByShoppingCartId(Long shoppingCartId, PageRequest pageRequest);
    boolean existsByShoppingCartIdAndProductId(Long shoppingCartId, Long productId);
}
