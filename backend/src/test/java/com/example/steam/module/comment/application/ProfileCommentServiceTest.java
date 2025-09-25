package com.example.steam.module.comment.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.domain.ProfileComment;
import com.example.steam.module.comment.repository.ProfileCommentRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileCommentServiceTest {
    private Member member;
    private Member profileMember;
    private ProfileComment profileComment;

    @InjectMocks private ProfileCommentService profileCommentService;
    @Mock private ProfileCommentRepository profileCommentRepository;
    @Mock private MemberRepository memberRepository;

    @BeforeEach
    void setUp(){
        int memberNum = 1, profileMemberNum = 2;
        String profileCommentContent = "profileCommentContent1";
        member = Member.makeSample(memberNum);
        profileMember = Member.makeSample(profileMemberNum);
        profileComment = ProfileComment.of(member, profileCommentContent, profileMember);
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(profileMember, "id", 2L);
        ReflectionTestUtils.setField(profileComment, "id", 1L);

    }

    @Test
    void makeProfileCommentTest() {
        //given
        given(profileCommentRepository.save(any(ProfileComment.class))).willReturn(profileComment);

        //when
        ProfileComment result = profileCommentService.makeProfileComment(member, profileMember, profileComment.getContent());

        //then
        assertThat(result.getId()).isEqualTo(profileComment.getId());
        verify(profileCommentRepository).save(any(ProfileComment.class));
    }

    @Test
    void findProfileCommentByProfileMemberIdTest() {
        //given
        int sampleNum2 = 2, sampleNum3 = 3;
        int pageNo = 0;
        PageRequest pageRequest = PageRequest.of(pageNo, PageConst.PROFILE_COMMENT_PAGE_SIZE);
        List<ProfileComment> profileComments = new ArrayList<>();
        profileComments.add(ProfileComment.makeSample(sampleNum2, member, profileMember));
        profileComments.add(ProfileComment.makeSample(sampleNum3, member, profileMember));
        Page<ProfileComment> profileCommentPage = new PageImpl<>(profileComments, pageRequest, 10);
        given(profileCommentRepository.findAllByProfileMemberId(profileMember.getId(), pageRequest)).willReturn(profileCommentPage);

        //when
        Page<ProfileComment> result = profileCommentService.findProfileCommentByProfileMemberId(profileMember.getId(), pageNo);

        //then
        assertThat(result.stream().allMatch(pc -> pc.getProfileMember().getId().equals(profileMember.getId()))).isTrue();
        verify(profileCommentRepository).findAllByProfileMemberId(profileMember.getId(), pageRequest);
    }

    @Test
    @DisplayName("요청자와 댓글 소유자가 같은 경우 성공")
    void deleteProfileCommentTest1() {
        //given
        given(profileCommentRepository.findById(profileComment.getId())).willReturn(Optional.of(profileComment));

        //when
        boolean result = profileCommentService.deleteProfileComment(profileComment.getId(), member.getId());

        //then
        assertThat(result).isTrue();
        verify(profileCommentRepository).delete(any(ProfileComment.class));
    }

    @Test
    @DisplayName("요청자와 댓글 소유자가 다른 경우 실패")
    void deleteProfileCommentTest2() {
        //given
        int otherNumber = 5;
        Member otherMember = Member.makeSample(otherNumber);
        ReflectionTestUtils.setField(otherMember, "id", 5L);
        given(profileCommentRepository.findById(profileComment.getId())).willReturn(Optional.of(profileComment));

        //when
        boolean result = profileCommentService.deleteProfileComment(profileComment.getId(), otherMember.getId());


        //then
        assertThat(result).isFalse();
        verify(profileCommentRepository, never()).delete(any(ProfileComment.class));
    }
}