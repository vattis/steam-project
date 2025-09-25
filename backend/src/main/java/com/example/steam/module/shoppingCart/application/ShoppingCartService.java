package com.example.steam.module.shoppingCart.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.order.domain.Orders;
import com.example.steam.module.order.repository.OrdersRepository;
import com.example.steam.module.product.application.ProductService;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import com.example.steam.module.shoppingCart.domain.ShoppingCart;
import com.example.steam.module.shoppingCart.domain.ShoppingCartProduct;
import com.example.steam.module.shoppingCart.repository.ShoppingCartProductRepository;
import com.example.steam.module.shoppingCart.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartProductRepository shoppingCartProductRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final MemberService memberService;
    private final ProductService productService;

    //장바구니 목록 조회
    public Page<ShoppingCartProduct> getShoppingCartProducts(Long memberId, int pageNo){
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        PageRequest pageRequest = PageRequest.of(pageNo, PageConst.SHOPPING_CART_PRODUCT_PAGE_SIZE);
        return shoppingCartProductRepository.findAllByShoppingCartId(member.getShoppingCart().getId(), pageRequest);
    }

    //id로 장바구니 상품 찾기
    public ShoppingCartProduct findById(Long shoppingCartProductId){
        return shoppingCartProductRepository.findById(shoppingCartProductId).orElseThrow(NoSuchElementException::new);
    }

    //장바구니 상품 삭제
    public void removeShoppingCartProduct(ShoppingCartProduct shoppingCartProduct, Long memberId){
        if(!shoppingCartProduct.getShoppingCart().getMember().getId().equals(memberId)){
            log.info("잘못된 ShoppingCartProduct 삭제 요청::사용자 불일치");
            return;
        }
        shoppingCartProductRepository.delete(shoppingCartProduct);
    }

    //장바구니 상품 추가
    public void addShoppingCartProduct(Long memberId, Long productId){
        if(shoppingCartProductRepository.existsByShoppingCartIdAndProductId(memberId, productId)){
            log.info("잘못된 ShoppingCartProduct 추가 요청::중복된 product 요청");
            return;
        }
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        Product product = productRepository.findById(productId).orElseThrow(NoSuchElementException::new);
        ShoppingCartProduct shoppingCartProduct = ShoppingCartProduct.of(member.getShoppingCart(), product);
        member.getShoppingCart().addShoppingCartProduct(shoppingCartProduct);
        shoppingCartProductRepository.save(shoppingCartProduct);
    }

    //장바구니를 주문으로 변경
    public Orders makeShoppingCartToOrder(Member member){
        ShoppingCart shoppingCart = member.getShoppingCart();
        return ordersRepository.save(shoppingCart.toOrders());
    }
}
