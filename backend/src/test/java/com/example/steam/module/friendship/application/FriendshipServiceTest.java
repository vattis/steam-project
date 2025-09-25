package com.example.steam.module.friendship.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.friendship.domain.Friendship;
import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.friendship.repository.FriendshipRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {
    @InjectMocks FriendshipService friendshipService;
    @Mock FriendshipRepository friendshipRepository;
    @Mock MemberRepository memberRepository;

    @Test
    void getFriends() {
        //given
        Member member = Member.makeSample(1);
        ReflectionTestUtils.setField(member, "id", 1L);
        Page<Friendship> friendships = Page.empty();
        Pageable pageable = PageRequest.of(0, PageConst.FRIENDS_PAGE_SIZE);
        given(friendshipRepository.findAllByFromMember_IdAndState(member.getId(), FriendshipState.FRIENDS, pageable)).willReturn(friendships);

        //when
        Page<Friendship> friendshipPageResult = friendshipService.getFriends(member.getId(), pageable);

        //then
        assertThat(friendshipPageResult).isEqualTo(friendships);
    }

    @Test
    @DisplayName("이미 친구 관계가 성립된 경우 친구 신청")
    void addFriend1() {
        //given
        Member friendMember1 = Member.makeSample(1);
        Member friendMember2 = Member.makeSample(2);
        ReflectionTestUtils.setField(friendMember1, "id", 1L);
        ReflectionTestUtils.setField(friendMember2, "id", 2L);

        given(friendshipRepository.existsByFromMemberIdAndToMemberId(friendMember1.getId(), friendMember2.getId())).willReturn(true);
        given(friendshipRepository.findByFromMemberIdAndToMemberId(friendMember1.getId(), friendMember2.getId())).willReturn(Optional.of(Friendship.of(friendMember1, friendMember2, FriendshipState.FRIENDS)));

        //when
        friendshipService.inviteFriend(friendMember1.getId(), friendMember2.getId());

        //then
        verify(memberRepository, never()).findById(any(Long.class));
        verify(friendshipRepository, never()).save(any(Friendship.class));
    }

    @Test
    @DisplayName("친구 관계가 아닌 경우의 친구 신청")
    void inviteFriend1(){
        //given
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        Member fromMember = Member.makeSample(Math.toIntExact(fromMemberId));
        Member toMember = Member.makeSample(Math.toIntExact(toMemberId));
        ReflectionTestUtils.setField(fromMember, "id", fromMemberId);
        ReflectionTestUtils.setField(toMember, "id", toMemberId);
        Optional<Member> optionalFromMember = Optional.of(fromMember); //미리 생성해두는게 핵심
        Optional<Member> optionalToMember = Optional.of(toMember);

        given(friendshipRepository.existsByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())).willReturn(false);
        //미리 만들어둔 인스턴스를 테스트 메서드 내부로 넣기
        given(memberRepository.findById(fromMember.getId())).willReturn(optionalFromMember);
        given(memberRepository.findById(toMember.getId())).willReturn(optionalToMember);

        Friendship friendship = Friendship.of(fromMember, toMember, FriendshipState.INVITE_SENT);
        given(friendshipRepository.save(any(Friendship.class))).willReturn(friendship);

        //when
        Friendship resultFriendship = friendshipService.inviteFriend(fromMember.getId(), toMember.getId());

        //then
        //만들어둔 인스턴스를 통해 서로 친구관계 교환이 됐나 확인
        assertThat(resultFriendship.getFromMember()).isEqualTo(optionalFromMember.get());
        assertThat(resultFriendship.getToMember()).isEqualTo(optionalToMember.get());
        assertThat(optionalFromMember.get().getFriendships().get(0).getToMember().getId()).isEqualTo(toMemberId);
        //save()가 2번 호출 되었는지 확인
        verify(friendshipRepository, times(2)).save(any(Friendship.class));
    }

    @Test
    @DisplayName("이미 친구 관계인 경우 null 반환")
    void inviteFriend2() {
        //given
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        Member fromMember = Member.makeSample(Math.toIntExact(fromMemberId));
        Member toMember = Member.makeSample(Math.toIntExact(toMemberId));
        ReflectionTestUtils.setField(fromMember, "id", fromMemberId);
        ReflectionTestUtils.setField(toMember, "id", toMemberId);
        Optional<Member> optionalFromMember = Optional.of(fromMember); //미리 생성해두는게 핵심
        Optional<Member> optionalToMember = Optional.of(toMember);
        Friendship friendship = Friendship.of(fromMember, toMember, FriendshipState.FRIENDS);

        given(friendshipRepository.existsByFromMemberIdAndToMemberId(fromMemberId, toMemberId)).willReturn(true);
        given(friendshipRepository.findByFromMemberIdAndToMemberId(fromMemberId, toMemberId)).willReturn(Optional.of(friendship));

        //when
        Friendship resultFriendship = friendshipService.inviteFriend(fromMemberId, toMemberId);

        //then
        assertThat(resultFriendship).isNull();
    }

    @Test
    @DisplayName("중복으로 친구 신청을 한 경우 null 반환")
    void inviteFriend3() {
        //given
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        Member fromMember = Member.makeSample(Math.toIntExact(fromMemberId));
        Member toMember = Member.makeSample(Math.toIntExact(toMemberId));
        ReflectionTestUtils.setField(fromMember, "id", fromMemberId);
        ReflectionTestUtils.setField(toMember, "id", toMemberId);
        Optional<Member> optionalFromMember = Optional.of(fromMember); //미리 생성해두는게 핵심
        Optional<Member> optionalToMember = Optional.of(toMember);
        Friendship friendship = Friendship.of(fromMember, toMember, FriendshipState.INVITE_SENT);

        given(friendshipRepository.existsByFromMemberIdAndToMemberId(fromMemberId, toMemberId)).willReturn(true);
        given(friendshipRepository.findByFromMemberIdAndToMemberId(fromMemberId, toMemberId)).willReturn(Optional.of(friendship));

        //when
        Friendship resultFriendship = friendshipService.inviteFriend(fromMemberId, toMemberId);

        //then
        assertThat(resultFriendship).isNull();
    }

    @Test
    @DisplayName("친구 수락 테스트")
    void acceptFriendTest(){
        //given
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        Member fromMember = Member.makeSample(Math.toIntExact(fromMemberId));
        Member toMember = Member.makeSample(Math.toIntExact(toMemberId));
        Friendship friendship1 = Friendship.of(fromMember, toMember, FriendshipState.INVITED);
        Friendship friendship2 = Friendship.createReverseFriendship(friendship1, FriendshipState.INVITE_SENT);
        ReflectionTestUtils.setField(fromMember, "id", fromMemberId);
        ReflectionTestUtils.setField(toMember, "id", toMemberId);
        ReflectionTestUtils.setField(friendship1, "id", 1L);
        //given(friendshipRepository.findById(friendship1.getId())).willReturn(Optional.of(friendship1));
        given(friendshipRepository.findByFromMemberIdAndToMemberId(fromMemberId, toMemberId)).willReturn(Optional.of(friendship1));
        given(friendshipRepository.findByFromMemberIdAndToMemberId(toMemberId, fromMemberId)).willReturn(Optional.of(friendship2));

        //when
        friendshipService.acceptFriend(fromMemberId, toMemberId);

        //then
        assertThat(toMember.getFriendships().get(0).getToMember()).isEqualTo(fromMember);
    }

    @Test
    @DisplayName("이미 수락된 친구 관계에 수락 요청을 할 경우")
    void acceptFriendTest2(){
        //given
        Long fromMemberId = 1L;
        Long toMemberId = 2L;
        Member fromMember = Member.makeSample(Math.toIntExact(fromMemberId));
        Member toMember = Member.makeSample(Math.toIntExact(toMemberId));
        Friendship friendship1 = Friendship.of(fromMember, toMember, FriendshipState.FRIENDS);
        Friendship friendship2 = Friendship.createReverseFriendship(friendship1, FriendshipState.FRIENDS);
        ReflectionTestUtils.setField(fromMember, "id", fromMemberId);
        ReflectionTestUtils.setField(toMember, "id", toMemberId);
        ReflectionTestUtils.setField(friendship1, "id", 1L);
        //given(friendshipRepository.findById(friendship1.getId())).willReturn(Optional.of(friendship1));
        given(friendshipRepository.findByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId())).willReturn(Optional.of(friendship1));
        //when
        friendshipService.acceptFriend(fromMemberId, toMemberId);

        //then
        verify(friendshipRepository, times(0)).save(any(Friendship.class));
    }

    @Test
    void removeFriendship() {
        //given
        Member friendMember1 = Member.makeSample(1);
        Member friendMember2 = Member.makeSample(2);
        Friendship friendship = Friendship.of(friendMember1, friendMember2, FriendshipState.FRIENDS);
        ReflectionTestUtils.setField(friendMember1, "id", 1L);
        ReflectionTestUtils.setField(friendMember2, "id", 2L);
        ReflectionTestUtils.setField(friendship, "id", 1L);
        //given(friendshipRepository.findById(friendship.getId())).willReturn(Optional.of(friendship));
        given(friendshipRepository.findByFromMemberIdAndToMemberId(friendMember1.getId(), friendMember2.getId())).willReturn(Optional.of(friendship));
        //when
        friendshipService.removeFriendship(friendMember1.getId(), friendMember2.getId());

        //then
        verify(friendshipRepository, times(1)).deleteByFromMemberIdAndToMemberId(friendMember1.getId(), friendMember2.getId());
        verify(friendshipRepository, times(1)).deleteByFromMemberIdAndToMemberId(friendMember2.getId(), friendMember1.getId());
    }
}