package com.example.steam.module.product.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.discount.domain.Discount;
import com.example.steam.module.discount.repository.DiscountRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.dto.SimpleProductBannerDto;
import org.junit.jupiter.api.BeforeEach;
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
class ProductRepositoryTest {

    @Autowired private CompanyRepository companyRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private DiscountRepository discountRepository;

    @BeforeEach
    void setUp() {
        List<Company> companies = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Discount> discounts = new ArrayList<>();

        for(int i = 1; i <= 10; i++) {
            companies.add(Company.makeSample(i));
        }
        for(int i = 1; i <= 100; i++){
            Product product = Product.makeSample(i, companies.get(i%10));
            products.add(product);
            discounts.add(Discount.makeSample(i, product));
        }
        companyRepository.saveAll(companies);
        productRepository.saveAll(products);
        discountRepository.saveAll(discounts);
    }

    @Test
    void findAllByOrderByDownloadNum() {
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_PAGE_SIZE);

        //when
        Page<SimpleProductBannerDto> productPage1 = productRepository.findAllByOrderByDownloadNum(pageRequest);

        //then
        assertThat(productPage1.getTotalElements()).isEqualTo(100);
    }

    @Test
    void findAllByNameContaining() {
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_PAGE_SIZE);
        String nameSearchWord = "name3";

        //when
        Page<Product> productPage2 = productRepository.findAllByNameContaining(nameSearchWord, pageRequest);

        //then
        assertThat(productPage2.getTotalElements()).isEqualTo(11);
        assertThat(productPage2.stream().allMatch(p -> p.getName().contains(nameSearchWord))).isTrue();
    }

    @Test
    void findAllByCompanyNameContaining() {
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_PAGE_SIZE);
        String companyNameSearchWord = "companyName4";

        //when
        Page<Product> productPage3 = productRepository.findAllByCompanyNameContaining(companyNameSearchWord, pageRequest);

        //then
        assertThat(productPage3.getTotalElements()).isEqualTo(10);
        assertThat(productPage3.stream().allMatch(p -> p.getCompany().getName().contains(companyNameSearchWord))).isTrue();
    }

    @Test
    void findAllByNameOrCompanyNameContaining() {
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_PAGE_SIZE);
        String allSearchWord = "ame5";

        //when
        Page<Product> productPage4 = productRepository.findAllByNameOrCompanyNameContaining(allSearchWord, pageRequest);

        //then
        assertThat(productPage4.getTotalElements()).isEqualTo(20);
        assertThat(productPage4.stream().allMatch(p->p.getCompany().getName().contains(allSearchWord) || p.getName().contains(allSearchWord))).isTrue();
    }
}