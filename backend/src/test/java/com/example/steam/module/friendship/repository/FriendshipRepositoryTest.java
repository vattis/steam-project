package com.example.steam.module.friendship.repository;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.friendship.domain.Friendship;
import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FriendshipRepositoryTest {
    @Autowired private FriendshipRepository friendshipRepository;
    @Autowired private MemberRepository memberRepository;
    private List<Member> members;
    private List<Member> otherMembers;
    private List<Friendship> friendships;
    private List<Friendship> notAcceptedFriendships;
    @BeforeEach
    void init(){
        List<Member> members = new ArrayList<>();
        otherMembers = new ArrayList<>();
        List<Friendship> friendships = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            members.add(memberRepository.save(Member.makeSample(i)));
        }
        for(int i = 11; i <= 15; i++){
            otherMembers.add(Member.makeSample(i));
        }
        memberRepository.saveAll(members);
        memberRepository.saveAll(otherMembers);
        for(int i = 0; i <= 8; i++){
            Friendship friendship1 = Friendship.of(members.get(i), members.get(i+1), FriendshipState.FRIENDS);
            Friendship friendship2 = Friendship.createReverseFriendship(friendship1, FriendshipState.FRIENDS);
            friendships.add(friendship1);
            friendships.add(friendship2);
        }
        for(int i = 0; i <= 7; i++){
            Friendship friendship1 = Friendship.of(members.get(i), members.get(i+2), FriendshipState.FRIENDS);
            Friendship friendship2 = Friendship.createReverseFriendship(friendship1, FriendshipState.FRIENDS);
            friendships.add(friendship1);
            friendships.add(friendship2);
        }
        friendshipRepository.saveAll(friendships);
        this.members = members;
        this.friendships = friendships;

        Friendship invitedSentFriendship1 = Friendship.of(otherMembers.get(0), otherMembers.get(3), FriendshipState.INVITE_SENT);
        Friendship invitedFriendship1 = Friendship.createReverseFriendship(invitedSentFriendship1, FriendshipState.INVITED);
        friendshipRepository.save(invitedSentFriendship1);
        friendshipRepository.save(invitedFriendship1);
        Friendship invitedSentFriendship2 = Friendship.of(otherMembers.get(0), otherMembers.get(4), FriendshipState.INVITE_SENT);
        Friendship invitedFriendship2 = Friendship.createReverseFriendship(invitedSentFriendship2, FriendshipState.INVITED);
        friendshipRepository.save(invitedSentFriendship2);
        friendshipRepository.save(invitedFriendship2);
    }

    @Test
    void findAllByFromMemberId() {
        //given

        //when
        List<Friendship> friendshipPage1 = friendshipRepository.findAllByFromMemberId(members.get(0).getId());

        //then
        assertThat(friendshipPage1.size()).isEqualTo(2);
        friendshipPage1.forEach(a->assertThat(a.getFromMember()).isEqualTo(members.get(0)));
    }

    @Test
    void existsByFromMemberIdAndToMemberId() {
        //given
        Member toMember1 = members.get(5);
        Member fromMember1 = members.get(6);

        Member toMember2 = members.get(1);
        Member fromMember2 = members.get(7);

        //when
        Boolean resultTrue = friendshipRepository.existsByFromMemberIdAndToMemberId(fromMember1.getId(), toMember1.getId());
        Boolean resultFalse = friendshipRepository.existsByFromMemberIdAndToMemberId(fromMember2.getId(), toMember2.getId());

        //then
        assertThat(resultTrue).isTrue();
        assertThat(resultFalse).isFalse();
    }

    @Test
    void findByFromMemberIdAndToMemberId() {
        //given
        Member fromMember1 = members.get(5);
        Member toMember1 = members.get(6);

        //when
        Optional<Friendship> friendship = friendshipRepository.findByFromMemberIdAndToMemberId(fromMember1.getId(), toMember1.getId());

        //then
        assertThat(friendship.get().getFromMember()).isEqualTo(fromMember1);
        assertThat(friendship.get().getToMember()).isEqualTo(toMember1);
    }

    @Test
    void findAllByFromMember_IdAndStateTest(){
        //given
        Member member1 = otherMembers.get(0);
        Member member2 = members.get(0);
        Pageable pageable = PageRequest.of(0, PageConst.FRIENDS_PAGE_SIZE);

        //when
        Page<Friendship> result1 = friendshipRepository.findAllByFromMember_IdAndState(member1.getId(), FriendshipState.INVITE_SENT, pageable);
        Page<Friendship> result2 = friendshipRepository.findAllByFromMember_IdAndState(member2.getId(), FriendshipState.FRIENDS, pageable);


        //then
        assertThat(result1.stream().allMatch(f -> f.getFromMember().getId().equals(member1.getId()) && f.getState().equals(FriendshipState.INVITE_SENT)));
        assertThat(result2.stream().allMatch(f -> f.getFromMember().getId().equals(member2.getId()) && f.getState().equals(FriendshipState.FRIENDS)));
        assertThat(result1.getTotalElements()).isEqualTo(2);
        assertThat(result2.getTotalElements()).isEqualTo(2);
    }



    @Test
    void deleteByFromMemberIdAndToMemberId() {
        //given
        Member fromMember1 = members.get(5);
        Member toMember1 = members.get(6);

        //when
        friendshipRepository.deleteByFromMemberIdAndToMemberId(fromMember1.getId(), toMember1.getId());
        Boolean result = friendshipRepository.existsByFromMemberIdAndToMemberId(fromMember1.getId(), toMember1.getId());

        //then
        assertThat(result).isFalse();
    }

    @Test
    void findAllByToMemberAndStateTest(){
        //given

        //when
        List<Friendship> friendships1 = friendshipRepository.findAllByToMemberAndState(otherMembers.get(3), FriendshipState.INVITE_SENT);

        //then
        assertThat(friendships1.size()).isEqualTo(1);
        assertThat(friendships1.get(0).getToMember().getId()).isEqualTo(otherMembers.get(3).getId());
        assertThat(friendships1.get(0).getState()).isEqualTo(FriendshipState.INVITE_SENT);
    }

    @Test
    void findAllByFromMemberAndAcceptedIsFalse(){
        //given

        //when
        List<Friendship> friendships1 = friendshipRepository.findAllByFromMemberAndState(otherMembers.get(0), FriendshipState.INVITE_SENT);

        //then
        assertThat(friendships1.size()).isEqualTo(2);
        assertThat(friendships1.stream().allMatch(f -> f.getFromMember().getId().equals(otherMembers.get(0).getId()))).isTrue();
        assertThat(friendships1.stream().allMatch(f->f.getState().equals(FriendshipState.INVITE_SENT))).isTrue();
    }
}