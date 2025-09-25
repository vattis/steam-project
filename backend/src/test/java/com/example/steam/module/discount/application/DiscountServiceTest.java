package com.example.steam.module.discount.application;

import com.example.steam.module.discount.domain.Discount;
import com.example.steam.module.discount.repository.DiscountRepository;
import com.example.steam.module.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {
    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private Product product;

    @InjectMocks
    private DiscountService discountService;

    @Test
    void makeDiscount() {
        // given
        LocalDateTime now = LocalDateTime.now();
        int discountRate = 20;
        int discountPrice = 1000;

        Discount dummyDiscount = new Discount(1L, product, now, now.plusDays(3), discountRate, true, false);

        // product.assignDiscount(discount)를 내부에서 호출하므로 반드시 mock이 필요
        doNothing().when(product).assignDiscount(any(Discount.class));

        when(discountRepository.save(any(Discount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 discount 그대로 반환

        // when
        Discount result = discountService.makeDiscount(product, now, now.plusDays(3), discountRate, discountPrice);

        // then
        assertNotNull(result);
        assertEquals(discountRate, result.getDiscountRate());
        assertEquals(now, result.getStartTime());
        verify(discountRepository).save(any(Discount.class));
        verify(product).assignDiscount(any(Discount.class));
    }
}