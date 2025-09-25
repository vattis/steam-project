package com.example.steam.module.member.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.comment.dto.ProfileCommentDto;
import com.example.steam.module.comment.repository.ProfileCommentRepository;
import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.friendship.dto.SimpleFriendshipDto;
import com.example.steam.module.friendship.repository.FriendshipRepository;
import com.example.steam.module.friendship.repository.projection.FriendAndStatusView;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.dto.*;
import com.example.steam.module.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberGameService memberGameService;
    private final ProfileCommentRepository profileCommentRepository;
    private final FriendshipRepository friendshipRepository;
    //회원가입 서비스
    public Member addMember(SignUpForm signUpForm){
        if(!isValid(signUpForm)){
            throw(new RuntimeException());
        }
        Member member = Member.of(signUpForm.getNickname(), signUpForm.getEmail(), signUpForm.getPassword());
        return memberRepository.save(member);
    }

    //회원 정보 수정
    //@CachePut(value = "SpringCache", key = "#signUpForm.email")
    public Member updateMember(SignUpForm signUpForm){
        if(!signUpForm.isValid()){
            throw new RuntimeException();
        }
        Member member = memberRepository.findByEmail(signUpForm.getEmail()).orElseThrow(NoSuchElementException::new);
        return member.update(signUpForm.getNickname(), signUpForm.getPassword());
    }

    //회원 정보 검색
    public Member findMember(Long id){
        return memberRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    //이메일을 통한 회원 검색
    @Cacheable(cacheNames="login-member::email:memberDto",
            key = "T(org.springframework.util.DigestUtils).md5DigestAsHex(#email.bytes)",
            unless = "#result == null")
    public SimpleMemberDto findMemberDtoByEmail(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        return SimpleMemberDto.from(member);
    }


    //회원가입 유효성 검증
    public boolean isValid(SignUpForm signUpForm){
        return signUpForm.isValid() && !memberRepository.existsByEmail(signUpForm.getEmail());
    }

    //회원 가입 이메일 인증 코드 만들기
    public int createAuthCode(){
        Random random = new Random();
        return random.nextInt(900000)+100000;
    }

    //프로필 정보 검색 및 dto 생성
    public ProfileDto getProfile(Long profileMemberId, PageRequest pageRequest){
        Member profileMember = memberRepository.findById(profileMemberId).orElseThrow(NoSuchElementException::new);
        List<MemberGameDto> memberGames = memberGameService.findTop5DtoByMember(profileMember);
        Page<ProfileCommentDto> profileCommentPage = profileCommentRepository.findDtoByProfileMember(profileMember, pageRequest);
        List<SimpleFriendshipDto> friendships = friendshipRepository.findAllByFromMemberId(profileMemberId).stream().map(SimpleFriendshipDto::from).toList();
        return ProfileDto.of(profileMember, memberGames, profileCommentPage, friendships);
    }

    //회원 검색(id와 닉네임 동시 검색)
    public Page<Member> searchMember(MemberSearch memberSearch, int pageNo, Principal principal){
        Pageable pageable = PageRequest.of(pageNo, PageConst.MEMBER_SEARCH_PAGE_SIZE);
        Page<Member> members = Page.empty();
        if(memberSearch.getSearchTag().equals("nickname")){
            members = memberRepository.findAllByNicknameContaining(memberSearch.getSearchWord(), pageable);
        }else if(memberSearch.getSearchTag().equals("id")){
            if(isLong(memberSearch.getSearchWord())){
                members = memberRepository.findById(Long.parseLong(memberSearch.getSearchWord()), pageable);
            }
        }
        return members;
    }

    public Page<SimpleMemberDto> attachFriendState(Page<SimpleMemberDto> simpleMemberDtoPage, Principal principal){
        if(principal == null){
            return simpleMemberDtoPage;
        }

        Member loginMember = memberRepository.findByEmail(principal.getName()).orElseThrow(NoSuchElementException::new);
        List<Long> memberIdList = simpleMemberDtoPage.map(SimpleMemberDto::getId).stream().toList();
        List<FriendAndStatusView> temp = friendshipRepository.findFriendIdAndStatus(loginMember.getId(), memberIdList);
        for(FriendAndStatusView f : temp){
            System.out.println(f.getFriendshipState());
        }
        Map<Long, FriendshipState> friendFlag = friendshipRepository.findFriendIdAndStatus(loginMember.getId(), memberIdList).stream()
                .collect(Collectors.toMap(FriendAndStatusView::getToMemberId, FriendAndStatusView::getFriendshipState
                        , (a, b) -> (a == FriendshipState.FRIENDS || b == FriendshipState.FRIENDS)
                                ? FriendshipState.FRIENDS
                                : FriendshipState.INVITE_SENT
                ));

        return simpleMemberDtoPage.map(m -> SimpleMemberDto.builder()
                        .id(m.getId())
                        .nickname(m.getNickname())
                        .avatarUrl(m.getAvatarUrl())
                        .friendshipState(friendFlag.getOrDefault(m.getId(), FriendshipState.NONE))
                                .build());
    }

    private boolean isLong(String s) {
        if (s == null) return false;
        try {
            Long.parseLong(s.trim());
            return true;            // 범위까지 검증됨 (오버플로우면 예외)
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
