package com.example.steam.module.member.repository;

import com.example.steam.module.member.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    void saveTest(){
        //given
        Member member = Member.makeSample(1);

        //when
        Member memberResult = memberRepository.save(member);

        //then
        assertThat(memberResult.getId()).isNotNull();
        assertThat(member.getNickname()).isEqualTo(memberResult.getNickname());
        assertThat(member.getEmail()).isEqualTo(memberResult.getEmail());
        assertThat(member.getPassword()).isEqualTo(memberResult.getPassword());
    }
    @Test
    void deleteTest(){
        //given
        Member member = Member.makeSample(1);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        memberRepository.delete(member);
        em.flush();
        em.clear();

        //then
        assertThat(memberRepository.existsById(member.getId())).isFalse();

    }
    @Test
    void findTest(){
        //given
        Member member = Member.makeSample(1);
        Member member1 = memberRepository.save(member);

        //when
        Member memberResult = memberRepository.findById(member1.getId()).orElse(null);

        //then
        assertThat(memberResult).isNotNull();
        assertThat(member.getNickname()).isEqualTo(memberResult.getNickname());
        assertThat(member.getEmail()).isEqualTo(memberResult.getEmail());
        assertThat(member.getPassword()).isEqualTo(memberResult.getPassword());
    }
}