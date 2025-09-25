package com.example.steam.module.shoppingCart.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import com.example.steam.module.shoppingCart.domain.ShoppingCart;
import com.example.steam.module.shoppingCart.domain.ShoppingCartProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShoppingCartRepositoryTest {
    @Autowired private ShoppingCartRepository shoppingCartRepository;
    @Autowired private ShoppingCartProductRepository shoppingCartProductRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach
    void init(){
        List<Member> members = new ArrayList<>();
        List<Company> companies = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            Member member = Member.makeSample(i);
            memberRepository.save(member);
            Company company = Company.makeSample(i);
            members.add(member);
            companies.add(company);
            companyRepository.save(company);
        }
        for(int i = 0; i < 10; i++){
            Member member = members.get(i);
            ShoppingCart shoppingCart = member.getShoppingCart();
            for(int j = 0; j < 5; j++){
                Product product = productRepository.save(Product.makeSample(i*5+j, companies.get(i)));
                shoppingCart.addShoppingCartProduct(ShoppingCartProduct.of(shoppingCart, product));
            }
        }
    }

    @Test
    @DisplayName("shoppingCart, shoppingCartProduct 저장 확인")
    void saveTest(){
        //given

        //when
        List<ShoppingCartProduct> shoppingCartProducts = shoppingCartProductRepository.findAll();
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();

        //then
        assertThat(shoppingCarts.size()).isEqualTo(10);
        assertThat(shoppingCartProducts.size()).isEqualTo(50);
    }

    @Test
    @DisplayName("findAllByShoppingCartIdTest")
    void findAllByShoppingCartIdTest(){
        //given
        Member member = memberRepository.findAll().get(0);
        Long shoppingCartId = member.getShoppingCart().getId();
        PageRequest pageRequest = PageRequest.of(0, PageConst.ORDERS_PRODUCT_PAGE_SIZE);

        //when
        Page<ShoppingCartProduct> shoppingCartProductPage = shoppingCartProductRepository.findAllByShoppingCartId(shoppingCartId, pageRequest);

        //then
        assertThat(shoppingCartProductPage.getTotalElements()).isEqualTo(5);

    }

    @Test
    @DisplayName("shoppingCartProduct 단건 삭제")
    void deleteTest1(){
        //given
        Member member = memberRepository.findAll().get(0);
        ShoppingCart shoppingCart = member.getShoppingCart();
        ShoppingCartProduct shoppingCartProduct = shoppingCart.getShoppingCartProducts().get(0);

        //when
        shoppingCartProductRepository.deleteById(shoppingCartProduct.getId());
        shoppingCart.removeShoppingCartProduct(shoppingCartProduct);

        //then
        assertThat(shoppingCartProductRepository.existsById(shoppingCartProduct.getId())).isFalse();
    }

    @Test
    @DisplayName("member 삭제시 shoppingCart와 shoppingCartProduct")
    void deleteTest2(){
        //given
        Member member = memberRepository.findAll().get(0);

        //when
        memberRepository.delete(member);

        //then
        assertThat(shoppingCartRepository.existsById(member.getShoppingCart().getId())).isFalse();
        assertThat(shoppingCartProductRepository.findAllByShoppingCartId(member.getShoppingCart().getId(), PageRequest.of(0, PageConst.SHOPPING_CART_PRODUCT_PAGE_SIZE)).getTotalElements()).isEqualTo(0);

    }

}