package com.example.steam.module.comment.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.domain.ProductComment;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductCommentRepositoryTest {
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private ProductCommentRepository productCommentRepository;
    @Autowired private EntityManager em;

    @BeforeEach
    void init(){
        List<Product> products = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        List<Company> companies = new ArrayList<>();
        List<ProductComment> productComments = new ArrayList<>();

        for(int i  = 1; i <= 10; i++){
            companies.add(Company.makeSample(i));
        }
        companyRepository.saveAll(companies);
        for(int i = 1; i <= 10; i++){
            members.add(Member.makeSample(i));
            products.add(Product.makeSample(i, companies.get(i-1)));
        }
        memberRepository.saveAll(members);
        productRepository.saveAll(products);
        for(int i = 0; i < 10; i++){
            Product product = products.get(i);
            for(int j = 1; j <= 5; j++){
                Member member = members.get((i+j)%10);
                productComments.add(ProductComment.makeSample(product, member, i*5+j));
            }
        }
        productCommentRepository.saveAll(productComments);
    }
    @Test
    void saveAndFindTest(){
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_COMMENT_PAGE_SIZE);
        Product product = productRepository.findAll().get(0);

        //when
        Page<ProductComment> productCommentPage = productCommentRepository.findAllByProductId(product.getId(), pageRequest);

        //then
        assertThat(productCommentPage.getTotalElements()).isEqualTo(5);
    }

    @Test
    void deleteTest1(){
        //given
        Product product = productRepository.findAll().get(0);
        ProductComment productComment = product.getProductComments().get(0);

        //when
        productCommentRepository.deleteById(productComment.getId());

        //then
        assertThat(productCommentRepository.findById(productComment.getId())).isEmpty();
    }

    @Test
    void deleteTest2(){
        //given
        Product product = productRepository.findAll().get(0);
        PageRequest pageRequest = PageRequest.of(0, PageConst.PRODUCT_COMMENT_PAGE_SIZE);
        Long productId = product.getId();

        //when
        productRepository.delete(product);


        //then
        assertThat(productCommentRepository.findAllByProductId(productId, pageRequest).isEmpty());
    }
}