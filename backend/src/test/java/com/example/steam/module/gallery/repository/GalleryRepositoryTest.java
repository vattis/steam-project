package com.example.steam.module.gallery.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.gallery.domain.Gallery;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GalleryRepositoryTest {
    @Autowired CompanyRepository companyRepository;
    @Autowired ProductRepository productRepository;
    @Autowired GalleryRepository galleryRepository;
    List<Company> companies = new ArrayList<>();
    List<Product> products = new ArrayList<>();
    List<Gallery> galleries = new ArrayList<>();

    @BeforeEach
    void setUp(){
        for(int i = 1; i <= 21; i++){
            Company company = Company.makeSample(i);
            companies.add(company);
            companyRepository.saveAll(companies);
            Product product = Product.makeSample(i, company);
            products.add(product);
            productRepository.saveAll(products);
            Gallery gallery = Gallery.makeSample(product);
            galleries.add(gallery);
            galleryRepository.saveAll(galleries);
        }
    }
    @Test
    void findByProduct_NameContainingTest() {
        //given
        String searchWord = "name1";
        int pageNo = 0;
        int resultCount = 0;
        Pageable pageable = PageRequest.of(pageNo, PageConst.PRODUCT_PAGE_SIZE);
        for(Gallery gallery : galleries){
            Product product = gallery.getProduct();
            if(product.getName().contains(searchWord)){
                resultCount++;
            }
        }

        //when
        Page<Gallery> result = galleryRepository.findByProduct_NameContaining(searchWord, pageable);

        //then
        assertThat(result.stream().allMatch(g->g.getProduct().getName().contains(searchWord))).isTrue();
        assertThat(result.getTotalElements()).isEqualTo(resultCount);
    }

    @Test
    void findByProduct_NameTest() {
        //given
        String name = "name1";

        //when
        Optional<Gallery> result =  galleryRepository.findByProduct_Name(name);


        //then
        assertThat(result.get().getProduct().getName()).isEqualTo(name);
    }
}