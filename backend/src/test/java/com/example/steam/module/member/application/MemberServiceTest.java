package com.example.steam.module.member.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.domain.ProfileComment;
import com.example.steam.module.comment.dto.ProfileCommentDto;
import com.example.steam.module.comment.repository.ProfileCommentRepository;
import com.example.steam.module.company.domain.Company;
import com.example.steam.module.friendship.repository.FriendshipRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.member.dto.MemberGameDto;
import com.example.steam.module.member.dto.ProfileDto;
import com.example.steam.module.member.dto.SignUpForm;
import com.example.steam.module.member.repository.MemberGameRepository;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock MemberGameRepository memberGameRepository;
    @Mock ProfileCommentRepository profileCommentRepository;
    @Mock FriendshipRepository friendshipRepository;
    @Mock MemberGameService memberGameService;
    @InjectMocks MemberService memberService;

    @Test
    void addMember() {
        //given
        SignUpForm correctSignUpForm = SignUpForm.of("email", "password", "password", "nickname");
        SignUpForm signUpFormWithNull = SignUpForm.of(null, "password", "password", "nickname");
        SignUpForm signUpFormWithPasswordErr = SignUpForm.of("email", "password", "passwordDiff", "nickname");

        Member correctMember = Member.of(correctSignUpForm.getNickname(), correctSignUpForm.getEmail(), correctSignUpForm.getPassword());
        ReflectionTestUtils.setField(correctMember, "id", 1L);

        given(memberRepository.save(any(Member.class))).willReturn(correctMember);
        given(memberRepository.existsByEmail(any(String.class))).willReturn(false);

        //when
        Member member = memberService.addMember(correctSignUpForm);
        RuntimeException exception1 = assertThrows(RuntimeException.class, ()->memberService.addMember(signUpFormWithNull));
        RuntimeException exception2 = assertThrows(RuntimeException.class, ()->memberService.addMember(signUpFormWithPasswordErr));

        //then
        assertThat(member).isEqualTo(correctMember);
        assertThat(exception1).isInstanceOf(RuntimeException.class);
        assertThat(exception2).isInstanceOf(RuntimeException.class);

    }

    @Test
    void updateMember() {
        //given
        SignUpForm newSignUpForm = SignUpForm.of("email", "newPassword", "newPassword", "newNickname");
        SignUpForm signUpFormWithNull = SignUpForm.of(null, "password", "password", "nickname");
        SignUpForm signUpFormWithPasswordErr = SignUpForm.of("email", "password", "passwordDiff", "nickname");
        Member oldMember = Member.of("nickname", "email", "password");

        given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(oldMember));

        //when
        Member memberResult = memberService.updateMember(newSignUpForm);
        RuntimeException exception1 = assertThrows(RuntimeException.class, ()->memberService.updateMember(signUpFormWithNull));
        RuntimeException exception2 = assertThrows(RuntimeException.class, ()->memberService.updateMember(signUpFormWithPasswordErr));

        //then
        assertThat(memberResult.getNickname()).isEqualTo(newSignUpForm.getNickname());
        assertThat(memberResult.getPassword()).isEqualTo(newSignUpForm.getPassword());
        assertThat(exception1).isInstanceOf(RuntimeException.class);
        assertThat(exception2).isInstanceOf(RuntimeException.class);
    }

    @Test
    void isValid() {
        //given
        SignUpForm newSignUpForm = SignUpForm.of("email", "newPassword", "newPassword", "newNickname");
        SignUpForm signUpFormWithNull = SignUpForm.of(null, "password", "password", "nickname");
        SignUpForm signUpFormWithPasswordErr = SignUpForm.of("email", "password", "passwordDiff", "nickname");
        given(memberRepository.existsByEmail(any(String.class))).willReturn(false);

        //when
        Boolean result1 = memberService.isValid(newSignUpForm);
        Boolean result2 = memberService.isValid(signUpFormWithNull);
        Boolean result3 = memberService.isValid(signUpFormWithPasswordErr);
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
    }

    @Test
    @DisplayName("프로필 dto 불러오기 테스트")
    void getProfileTest(){
        //given
        Long profileMemberId = 2L;
        PageRequest pageRequest = PageRequest.of(1, PageConst.PROFILE_COMMENT_PAGE_SIZE);
        Member profileMember = Member.makeSample(2);
        Member member1 = Member.makeSample(4);
        List<MemberGameDto> memberGames = new ArrayList<>();
        List<ProfileCommentDto> profileCommentDtos = new ArrayList<>();

        for(int i = 1; i <= 5; i++){
            MemberGame memberGame = MemberGame.of(Product.makeSample(i, Company.makeSample(i)), profileMember);
            memberGames.add(MemberGameDto.from(memberGame));
        }
        for(int i = 1; i <= 10; i++){
            profileCommentDtos.add(ProfileCommentDto.from(ProfileComment.makeSample(i, member1, profileMember)));
        }
        PageImpl<ProfileCommentDto> profileCommentDtoPage = new PageImpl<>(profileCommentDtos);
        given(memberRepository.findById(profileMemberId)).willReturn(Optional.of(profileMember));
        given(memberGameService.findTop5DtoByMember(profileMember)).willReturn(memberGames);
        given(profileCommentRepository.findDtoByProfileMember(profileMember, pageRequest)).willReturn(profileCommentDtoPage);

        //when
        ProfileDto profileDto = memberService.getProfile(profileMemberId, pageRequest);

        //then
        assertThat(profileDto.getProfileMember().getId()).isEqualTo(profileMember.getId());
        assertThat(profileDto.getProfileCommentPage().getTotalElements()).isEqualTo(10);
        assertThat(profileDto.getSimpleMemberGames().size()).isEqualTo(memberGames.size());
    }
}