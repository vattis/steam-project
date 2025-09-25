package com.example.steam.module.member.repository;

import com.example.steam.module.company.domain.Company;
import com.example.steam.module.company.repository.CompanyRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberGameRepositoryTest {
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private MemberGameRepository memberGameRepository;
    Member member1 = Member.makeSample(1);
    Member member2 = Member.makeSample(2);
    @BeforeEach
    void init(){
        List<Product> products = new ArrayList<>();
        List<Company> companies = new ArrayList<>();
        List<MemberGame> memberGames = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            Company company = Company.makeSample(i);
            companies.add(company);
            products.add(Product.makeSample(i, company));
        }
        for(int i = 1; i <= 6; i++){
            memberGames.add(MemberGame.of(products.get(i), member1));
        }
        for(int i = 3; i <= 9; i++){
            memberGames.add(MemberGame.of(products.get(i), member2));
        }
        member1 = memberRepository.save(member1);
        member2 = memberRepository.save(member2);
        companyRepository.saveAll(companies);
        productRepository.saveAll(products);
        memberGameRepository.saveAll(memberGames);
    }

    @Test
    @DisplayName("멤버가 가지고 있는 게임 검색")
    void findAllByMember() {
        //given

        //when
        List<MemberGame> memberGames1 = memberGameRepository.findAllByMember(member1);
        List<MemberGame> memberGames2 = memberGameRepository.findAllByMember(member2);

        //then
        assertThat(memberGames1.size()).isEqualTo(6);
        assertThat(memberGames2.size()).isEqualTo(7);
    }

    @Test
    @DisplayName("최근 플레이 순으로 5개 까지만 가져오기")
    void findTop5ByMemberOrderByLastPlayedTimeDesc() {
        //given

        //when
        List<MemberGame> memberGames = memberGameRepository.findTop5ByMemberOrderByLastPlayedTimeDesc(member1);

        //then
        assertThat(memberGames.size()).isEqualTo(5);
    }
}