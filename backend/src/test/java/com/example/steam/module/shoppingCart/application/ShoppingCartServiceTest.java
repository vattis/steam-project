package com.example.steam.module.shoppingCart.application;

import com.example.steam.module.company.domain.Company;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.order.domain.Orders;
import com.example.steam.module.order.repository.OrdersRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import com.example.steam.module.shoppingCart.domain.ShoppingCart;
import com.example.steam.module.shoppingCart.domain.ShoppingCartProduct;
import com.example.steam.module.shoppingCart.repository.ShoppingCartProductRepository;
import com.example.steam.module.shoppingCart.repository.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    ShoppingCartRepository shoppingCartRepository;
    @Mock
    ShoppingCartProductRepository shoppingCartProductRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    OrdersRepository ordersRepository;

    @InjectMocks
    ShoppingCartService shoppingCartService;


    @Test
    @DisplayName("member id를 받고 shoppingCartProductRepository.findAllByShoppingCartId를 제대로 호출 하는지 확인")
    void getShoppingCartProducts() {
        //given
        Member member = Member.makeSample(1);
        int pageNo = 0;
        ReflectionTestUtils.setField(member, "id", 1L);
        ShoppingCart shoppingCart = member.getShoppingCart();
        ReflectionTestUtils.setField(shoppingCart, "id", 1L);
        Optional<Member> optional = Optional.of(member);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when
        shoppingCartService.getShoppingCartProducts(member.getId(), pageNo);

        //then
        verify(shoppingCartProductRepository, times(1)).findAllByShoppingCartId(eq(shoppingCart.getId()), any(PageRequest.class));
    }

    @Test
    void removeShoppingCartProduct() {
        //given
        Member member = Member.makeSample(1);
        Product product = Product.makeSample(1, Company.makeSample(1));
        ShoppingCart shoppingCart = ShoppingCart.of(member);
        ShoppingCartProduct shoppingCartProduct = ShoppingCartProduct.of(shoppingCart, product);
        ReflectionTestUtils.setField(shoppingCartProduct, "id", 1L);
        ReflectionTestUtils.setField(member, "id", 1L);

        //when
        shoppingCartService.removeShoppingCartProduct(shoppingCartProduct, member.getId());

        //then
        verify(shoppingCartProductRepository, times(1)).delete(shoppingCartProduct);
    }

    @Test
    @DisplayName("장바구니에 상품 추가")
    void addShoppingCartProduct() {
        //given
        Member member = Member.makeSample(1);
        ReflectionTestUtils.setField(member, "id", 1L);
        Optional<Member> optionalMember = Optional.of(member);
        ReflectionTestUtils.setField(member.getShoppingCart(), "id", 1L);
        Product product = Product.makeSample(1, Company.makeSample(1));
        ReflectionTestUtils.setField(product, "id", 1L);
        given(memberRepository.findById(member.getId())).willReturn(optionalMember);
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        given(shoppingCartProductRepository.existsByShoppingCartIdAndProductId(member.getShoppingCart().getId(), product.getId())).willReturn(false);

        //when
        shoppingCartService.addShoppingCartProduct(member.getId(), product.getId());

        //then
        assertThat(optionalMember.get().getShoppingCart().getShoppingCartProducts().get(0).getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("장바구니에 상품 추가 실패::중복된 상품 추가 시도")
    void addShoppingCartProduct2() {
        //given
        Member member = Member.makeSample(1);
        ReflectionTestUtils.setField(member, "id", 1L);
        Optional<Member> optionalMember = Optional.of(member);
        ReflectionTestUtils.setField(member.getShoppingCart(), "id", 1L);
        Product product = Product.makeSample(1, Company.makeSample(1));
        ReflectionTestUtils.setField(product, "id", 1L);
        given(shoppingCartProductRepository.existsByShoppingCartIdAndProductId(member.getShoppingCart().getId(), product.getId())).willReturn(true);

        //when
        shoppingCartService.addShoppingCartProduct(member.getId(), product.getId());

        //then
        verify(shoppingCartProductRepository, never()).save(any(ShoppingCartProduct.class));
    }

    @Test
    @DisplayName("shoppingCart에 있는 게임을 지우고 Order로 변환되는지 확인")
    void makeShoppingCartToOrder() {
        //given
        List<Product> products = new ArrayList<>();
        Member member = Member.makeSample(1);
        ReflectionTestUtils.setField(member, "id", 1L);
        ShoppingCart shoppingCart = member.getShoppingCart();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        for(int i = 0; i < 10; i++){
            Product product = Product.makeSample(i, Company.makeSample(i));
            ReflectionTestUtils.setField(product, "id", (long)i+1);
            products.add(product);
            given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
            shoppingCartService.addShoppingCartProduct(member.getId(), product.getId());
        }
        Orders order = member.getShoppingCart().toOrders();
        ReflectionTestUtils.setField(order, "id", 1L);
        given(ordersRepository.save(any(Orders.class))).willReturn(order);
        //when
        Orders orderResult = shoppingCartService.makeShoppingCartToOrder(member);

        //then
        //shoppingCart를 orders로 전환한 후 shoppingCart를 비웠는지 확인
        assertThat(shoppingCart.getShoppingCartProducts().isEmpty()).isTrue();
        //order에 아이템이 들어갔는지 확인
        assertThat(orderResult.getOrderProducts().size()).isEqualTo(10);
    }
}