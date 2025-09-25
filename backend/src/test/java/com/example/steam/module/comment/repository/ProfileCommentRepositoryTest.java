package com.example.steam.module.comment.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.domain.ProfileComment;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
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
class ProfileCommentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProfileCommentRepository profileCommentRepository;

    private Member targetProfileMember;

    @BeforeEach
    void init(){
        List<Member> commentMember = new ArrayList<>();
        List<Member> profileMembers = new ArrayList<>();

        for(int i = 1; i <= 10; i++){
            commentMember.add(Member.makeSample(i));
        }
        for(int i = 11; i <= 20; i++){
            profileMembers.add(Member.makeSample(i));
        }
        memberRepository.saveAll(commentMember);
        memberRepository.saveAll(profileMembers);
        List<ProfileComment> profileComments = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            ProfileComment profileComment1 = ProfileComment.of(commentMember.get(i), "content"+i, profileMembers.get(i));
            ProfileComment profileComment2 = ProfileComment.of(commentMember.get(i), "content"+i, profileMembers.get(i));
            profileComments.add(profileComment1);
            profileComments.add(profileComment2);
        }
        profileCommentRepository.saveAll(profileComments);
        targetProfileMember = profileMembers.get(0);
    }

    @Test
    void saveAndFindTest(){
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PROFILE_COMMENT_PAGE_SIZE);
        Member member = memberRepository.findByEmail(targetProfileMember.getEmail()).orElseThrow();

        //when
        Page<ProfileComment> profileCommentPage = profileCommentRepository.findAllByProfileMemberId(member.getId(), pageRequest);

        //then
        assertThat(profileCommentPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void deleteTest(){
        //given
        PageRequest pageRequest = PageRequest.of(0, PageConst.PROFILE_COMMENT_PAGE_SIZE);
        Member member = memberRepository.findByEmail(targetProfileMember.getEmail()).orElseThrow();
        Page<ProfileComment> profileCommentPage = profileCommentRepository.findAllByProfileMemberId(member.getId(), pageRequest);

        //when
        profileCommentRepository.deleteAll(profileCommentPage);

        //then
        assertThat(profileCommentRepository.findAllByProfileMemberId(member.getId(), pageRequest).getTotalElements()).isEqualTo(0);
    }
}