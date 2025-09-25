package com.example.steam.module.product.presentation;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.application.ProductCommentService;
import com.example.steam.module.comment.dto.ProductCommentDto;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.application.ProductService;
import com.example.steam.module.product.domain.ProductSearch;
import com.example.steam.module.product.domain.ProductSearchTag;
import com.example.steam.module.product.dto.DetailProductDto;
import com.example.steam.module.product.dto.ProductWithCommentDto;
import com.example.steam.module.product.dto.SimpleProductBannerDto;
import com.example.steam.module.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductCommentService productCommentService;

    //상점 메인
    @GetMapping("/shop/product")
    String gotoShop(@RequestParam (name="filter", required = false) String filter,
                    @RequestParam (name="pageNo", required = false, defaultValue = "0") Integer pageNo,
                    Model model){
        String html;
        Pageable pageable;
        if(filter == null){
            pageable = PageRequest.of(pageNo, PageConst.PRODUCTS_BANNER_SIZE);
            html = "product/shop";
        } else if(filter.equals("popular")){
            pageable = PageRequest.of(pageNo, PageConst.PRODUCT_PAGE_SIZE);
            html = "product/popular-product";
        } else if(filter.equals("discount")){
            pageable = PageRequest.of(pageNo, PageConst.PRODUCT_PAGE_SIZE);
            html = "product/discount-product";
        } else{
            pageable = PageRequest.of(pageNo, PageConst.PRODUCTS_BANNER_SIZE);
            html = "product/shop";
        }
        Page<SimpleProductBannerDto> discountProducts = productService.findDiscountProductBanner(pageable);
        Page<SimpleProductBannerDto> popularProducts = productService.findTop5PopularProductBanner();
        model.addAttribute("discountProducts", discountProducts);
        model.addAttribute("popularProducts", popularProducts);
        return html;
    }

    @GetMapping("/product/{productId}")
    String gotoProduct(Model model, @PathVariable("productId") Long productId, @RequestParam(defaultValue = "0") int pageNo){
        DetailProductDto productDto = DetailProductDto.from(productService.findById(productId));
        Page<ProductCommentDto> productCommentDtoPage = productCommentService.findProductCommentByProductId(productId, pageNo).map(ProductCommentDto::from);
        model.addAttribute("product", productDto);
        model.addAttribute("productCommentDtoPage", productCommentDtoPage);
        model.addAttribute("productWithComment", new ProductWithCommentDto(productDto, productCommentDtoPage));
        return "product/product";
    }

    @GetMapping("/product/search")
    String searchProduct(Model model,
                         @RequestParam(name = "tag", required = false) String searchTag,
                         @RequestParam(name = "searchWord", required = false) String searchWord,
                         @RequestParam(name = "pageNo", defaultValue = "0", required = false) int pageNo){
        log.info("controller: search tag={}, searchWord={}", searchTag, searchWord);
        ProductSearch productSearch = ProductSearch.of(ProductSearchTag.makeTag(searchTag), searchWord);
        Page<SimpleProductBannerDto> products = productService.search(productSearch, pageNo);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("searchTag", searchTag);
        model.addAttribute("searchResults", products);
        return "product/searchProduct";
    }

}
