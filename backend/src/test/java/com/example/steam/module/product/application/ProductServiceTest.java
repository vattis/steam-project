package com.example.steam.module.product.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.domain.ProductSearch;
import com.example.steam.module.product.domain.ProductSearchTag;
import com.example.steam.module.product.dto.SimpleProductBannerDto;
import com.example.steam.module.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Test
    void save() {
        //given
        String productName = "testProductName";
        int productPrice = 10000;
        Company company = Company.makeSample(1);
        Product product = Product.of(productName, productPrice, company);
        ReflectionTestUtils.setField(company, "id", 1L);
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.save(any(Product.class))).willReturn(product);

        //when
        Product productResult = productService.save(productName, productPrice, company);

        //then
        assertThat(product).isEqualTo(productResult);
    }

    @Test
    void search() {
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_PAGE_SIZE);
        ProductSearch search = ProductSearch.of(ProductSearchTag.ALL, null);
        ProductSearch nameSearch = ProductSearch.of(ProductSearchTag.NAME, "name3");
        ProductSearch companySearch = ProductSearch.of(ProductSearchTag.COMPANY, "companyName4");
        ProductSearch allSearch1 = ProductSearch.of(ProductSearchTag.ALL, "ame3");
        int pageNo = 0;
        List<Company> companyList = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            companyList.add(Company.makeSample(i));
        }
        List<Product> productList = new ArrayList<>();
        List<Product> productList1 = new ArrayList<>();
        List<Product> productList2 = new ArrayList<>();
        List<Product> productList3 = new ArrayList<>();
        for(int i = 1; i <= 100; i++){
            Product product = Product.makeSample(i, companyList.get(i%10));
            if(product.getName().contains("name3")){
                productList1.add(product);
            }
            if(product.getCompany().getName().contains("companyName4")){
                productList2.add(product);
            }
            if(product.getName().contains("ame3") || product.getCompany().getName().contains("ame3")){
                productList3.add(product);
            }
            productList.add(product);
        }
        List<SimpleProductBannerDto> productBannerDtos = productList.stream().map(SimpleProductBannerDto::from).toList();
        given(productRepository.findAllByOrderByDownloadNum(pageRequest)).willReturn(new PageImpl<>(productBannerDtos, pageRequest, 100));
        given(productRepository.findAllByNameContaining(nameSearch.getSearchWord(), pageRequest)).willReturn(new PageImpl<>(productList1));
        given(productRepository.findAllByCompanyNameContaining(companySearch.getSearchWord(), pageRequest)).willReturn(new PageImpl<>(productList2));
        given(productRepository.findAllByNameOrCompanyNameContaining(allSearch1.getSearchWord(), pageRequest)).willReturn(new PageImpl<>(productList3));

        //when
        Page<SimpleProductBannerDto> pageResult1 = productService.search(search, pageNo);
        Page<SimpleProductBannerDto> pageResult2 = productService.search(nameSearch, pageNo);
        Page<SimpleProductBannerDto> pageResult3 = productService.search(companySearch, pageNo);
        Page<SimpleProductBannerDto> pageResult4 = productService.search(allSearch1, pageNo);


        //then
        assertThat(pageResult1.getTotalElements()).isEqualTo(100);
        assertThat(pageResult2.getTotalElements()).isEqualTo(11);
        assertThat(pageResult3.getTotalElements()).isEqualTo(10);
        assertThat(pageResult4.getTotalElements()).isEqualTo(20);
    }
}