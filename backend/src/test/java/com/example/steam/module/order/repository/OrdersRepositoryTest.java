package com.example.steam.module.order.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.order.domain.OrderProduct;
import com.example.steam.module.order.domain.Orders;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrdersRepositoryTest {
    @Autowired private OrdersRepository ordersRepository;
    @Autowired private OrderProductRepository orderProductRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private EntityManager em;

    @BeforeEach
    void init(){
        List<Member> members = new ArrayList<>();
        //List<Orders> orders = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        for(int i = 1; i <= 30; i++){
            Company company = Company.makeSample(i);
            company = companyRepository.save(company);
            products.add(Product.makeSample(i, company));
        }
        productRepository.saveAll(products);
        for(int i = 1; i <= 10; i++){
            members.add(Member.makeSample(i));
        }
        memberRepository.saveAll(members);

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 3; j++){
                Orders order = Orders.of(members.get(i));
                ordersRepository.save(order);
                for(int k = 0; k < 3; k++){
                    OrderProduct orderProduct = OrderProduct.of(order, products.get(i*3+k));
                    order.addOrderProduct(orderProduct);
                    orderProductRepository.save(orderProduct);
                }
            }
        }
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("특정 회원의 orders 모두 조회")
    void findAllByMemberIdOrderByCreatedDate() {
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.ORDERS_PAGE_SIZE);
        List<Member> members = memberRepository.findAll();

        //when
        Page<Orders> ordersPage = ordersRepository.findAllByMemberIdOrderByCreatedDate(members.get(0).getId(), pageRequest);


        //then
        assertThat(ordersPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("Orders와 OrderProduct 저장 테스트")
    void saveTest(){
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.ORDERS_PRODUCT_PAGE_SIZE);
        List<Member> members = memberRepository.findAll();
        Orders order = members.get(5).getOrders().get(0);

        //when
        Page<OrderProduct> orderProductPage = orderProductRepository.findAllByOrderId(order.getId(), pageRequest);

        //then
        assertThat(orderProductPage.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("Orders 삭제시 OrderProduct 까지 삭제 확인")
    void deleteTest(){
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.ORDERS_PRODUCT_PAGE_SIZE);
        Member member = memberRepository.findAll().get(0);
        List<Orders> orders = member.getOrders();
        Orders order = orders.get(0);
        member.getOrders().remove(order);

        //when
        ordersRepository.delete(order);

        //then
        assertThat(ordersRepository.findById(order.getId()).isPresent()).isFalse();
        assertThat(orderProductRepository.findAllByOrderId(order.getId(), pageRequest).getTotalElements()).isEqualTo(0L);
    }
}