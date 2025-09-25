package com.example.steam.module.product.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.domain.ProductSearch;
import com.example.steam.module.product.domain.ProductSearchTag;
import com.example.steam.module.product.dto.DetailProductDto;
import com.example.steam.module.product.dto.SimpleProductBannerDto;
import com.example.steam.module.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    //게임 등록
    public Product save(String name, int price, Company company){
        Product product = Product.of(name, price, company);
        return productRepository.save(product);
    }

    //전체 조회 및 검색
    public Page<SimpleProductBannerDto> search(ProductSearch productSearch, int pageNo){//정렬 기능 추가 예정
        PageRequest pageRequest = PageRequest.of(pageNo, PageConst.PRODUCT_PAGE_SIZE);
        if(productSearch.getSearchWord() == null || productSearch.getSearchWord().isEmpty()){ //전체 조회
            return productRepository.findAllByOrderByDownloadNum(pageRequest);
        }else if(productSearch.getSearchTag() == ProductSearchTag.NAME){ //게임 이름 조회
            return productRepository.findAllByNameContaining(productSearch.getSearchWord(), pageRequest).map(SimpleProductBannerDto::from);
        }else if(productSearch.getSearchTag() == ProductSearchTag.COMPANY){ //게임 개발사 조회
            return productRepository.findAllByCompanyNameContaining(productSearch.getSearchWord(), pageRequest).map(SimpleProductBannerDto::from);
        }else if(productSearch.getSearchTag() == ProductSearchTag.ALL){ //게임 이름 또는 개발사 조회
            return productRepository.findAllByNameOrCompanyNameContaining(productSearch.getSearchWord(), pageRequest).map(SimpleProductBannerDto::from);
        }
        return null;
    }

    //개발사로 게임 조회
    public Page<Product> findAllByCompany(Long companyId, int pageNo){
        PageRequest pageRequest = PageRequest.of(pageNo, PageConst.PRODUCT_PAGE_SIZE);
        return productRepository.findAllByCompanyId(companyId, pageRequest);
    }


    //게임 한개 조회
    public Product findById(Long id){
        return productRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    //게임 한개 조회 후 dto로 반환
    public DetailProductDto findDetailProductById(Long id){
        Product product = findById(id);
        return DetailProductDto.from(product);
    }

    //할인된 게임 조회
    public Page<SimpleProductBannerDto> findDiscountProductBanner(Pageable pageable){
        return productRepository.findDiscountProductBanner(pageable);
    }

    //다운로드가 많이된 게임 찾기
    public Page<SimpleProductBannerDto> findPopularProductBanner(Pageable pageable){
        return productRepository.findAllByOrderByDownloadNum(pageable);
    }

    //인기 게임 5개 찾기
    //@Cacheable(value="SpringCache", key = "'ProductBanner:top5'",
    //            unless = "#result == null || #result.isEmpty()")
    public Page<SimpleProductBannerDto> findTop5PopularProductBanner(){
        Pageable pageable = PageRequest.of(0, PageConst.PRODUCTS_BANNER_SIZE);
        return productRepository.findAllByOrderByDownloadNum(pageable);
    }

    //게임 삭제
    public void deleteById(Long id){
        productRepository.deleteById(id);
    }
}
