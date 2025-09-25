package com.example.steam.module.shoppingCart.presentation;

import com.example.steam.module.member.application.MemberService;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.SimpleMemberDto;
import com.example.steam.module.product.application.ProductService;
import com.example.steam.module.shoppingCart.application.ShoppingCartService;
import com.example.steam.module.shoppingCart.dto.SimpleShoppingCartProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final MemberService memberService;
    private final ProductService productService;

    @GetMapping("/shoppingCart")
    public String shoppingCart(@RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo,
                               Principal principal, Model model){
        SimpleMemberDto member = memberService.findMemberDtoByEmail(principal.getName());
        Page<SimpleShoppingCartProductDto> shoppingCartProductPage = shoppingCartService.getShoppingCartProducts(member.getId(), pageNo).map(SimpleShoppingCartProductDto::from);
        model.addAttribute("shoppingCartProductPage", shoppingCartProductPage);
        return "member/shopping-cart";
    }


}
