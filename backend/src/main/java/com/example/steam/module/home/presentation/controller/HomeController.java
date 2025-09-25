package com.example.steam.module.home.presentation.controller;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.product.application.ProductService;
import com.example.steam.module.product.dto.SimpleProductBannerDto;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String gotoHome(Model model){
        Page<SimpleProductBannerDto> products = productService.findTop5PopularProductBanner();
        model.addAttribute("products", products);
        return "main";
    }
}
