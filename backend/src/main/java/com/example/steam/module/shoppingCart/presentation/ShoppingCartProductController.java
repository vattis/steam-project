package com.example.steam.module.shoppingCart.presentation;

import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.SimpleMemberDto;
import com.example.steam.module.product.application.ProductService;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.shoppingCart.application.ShoppingCartService;
import com.example.steam.module.shoppingCart.domain.ShoppingCartProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartProductController {
    private final ShoppingCartService shoppingCartService;
    private final MemberService memberService;
    private final ProductService productService;

    @PostMapping("/shoppingCartProduct/{productId}")
    public String addShoppingCartProduct(@PathVariable("productId") Long productId, Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        Product product = productService.findById(productId);
        shoppingCartService.addShoppingCartProduct(member.getId(), product.getId());
        return "redirect:/product/" + productId;
    }

    @DeleteMapping("/shoppingCartProduct/{shoppingCartProductId}")
    public String deleteShoppingCartProduct(@PathVariable("shoppingCartProductId") Long shoppingCartProductId, Principal principal){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        ShoppingCartProduct shoppingCartProduct = shoppingCartService.findById(shoppingCartProductId);
        shoppingCartService.removeShoppingCartProduct(shoppingCartProduct, member.getId());
        return "redirect:/shoppingCart";
    }

}
