package com.example.steam.module.friendship.application;

import com.example.steam.module.friendship.domain.Friendship;
import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.friendship.repository.FriendshipRepository;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final MemberRepository memberRepository;

    //해당 유저의 친구 관계 조회
    public Page<Friendship> getFriends(Long fromMemberId, Pageable pageable){
        return friendshipRepository.findAllByFromMember_IdAndState(fromMemberId, FriendshipState.FRIENDS, pageable);
    }

    //친구 신청
    public Friendship inviteFriend(Long fromMemberId, Long toMemberId){ //
        if(friendshipRepository.existsByFromMemberIdAndToMemberId(fromMemberId, toMemberId)){ //이미 친구 신청을 했거나, 친구 관계인 경우
            Friendship friendship = friendshipRepository.findByFromMemberIdAndToMemberId(fromMemberId, toMemberId).orElseThrow(NoSuchElementException::new);
            if(friendship.getState() == FriendshipState.FRIENDS){
                log.info("이미 친구 관계인 유저의 친구 신청 fromId:{} toId:{}", fromMemberId, toMemberId);
            }else if(friendship.getState() == FriendshipState.INVITE_SENT){
                log.info("중복된 친구 신청 확인 fromId:{} toId:{}", fromMemberId, toMemberId);
            }else{
                log.info("부적절한 친구 관계 확인 fromId:{} toId:{}", fromMemberId, toMemberId);
            }
            return null;
        }

        Member fromMember = memberRepository.findById(fromMemberId).orElseThrow(NoSuchElementException::new); //친구 신청을 한 Member
        Member toMember = memberRepository.findById(toMemberId).orElseThrow(NoSuchElementException::new); //친구 신청을 받은 Member
        Friendship friendship1 = Friendship.of(fromMember, toMember, FriendshipState.INVITE_SENT); //초대한 친구 관계
        Friendship friendship2 = Friendship.createReverseFriendship(friendship1, FriendshipState.INVITED); //초대 받은 친구 관계

        fromMember.getFriendships().add(friendship1);
        toMember.getFriendships().add(friendship2);
        friendshipRepository.save(friendship2);
        return friendshipRepository.save(friendship1);

    }

    //친구 수락
    public void acceptFriend(Long fromMemberId, Long toMemberId){
        System.out.println("fromMemberId:" + fromMemberId + " toMemberId:" + toMemberId);
        Friendship friendship1 = friendshipRepository.findByFromMemberIdAndToMemberId(fromMemberId, toMemberId).orElseThrow(NoSuchElementException::new);
        if(friendship1 == null){
            throw new NoSuchElementException();
        }
        if(friendship1.getState()!= FriendshipState.INVITED){
            log.info("권한이 없는 친구 관계 수락 요청  fromId:{} toId:{} friendshipState:{}", friendship1.getFromMember().getId(), friendship1.getToMember().getId(), friendship1.getState());
            return;
        }
        Member member1 = friendship1.getFromMember();
        Member member2 = friendship1.getToMember();
        Friendship friendship2 = friendshipRepository.findByFromMemberIdAndToMemberId(member2.getId(), member1.getId()).orElseThrow(NoSuchElementException::new);

        friendship1.acceptFriendship();
        friendship2.acceptFriendship();

        member1.getFriendships().add(friendship1);
        member2.getFriendships().add(friendship2);
    }

    //초대 받은 친구 요청 확인
    public List<Friendship> getFriendRequest(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        return friendshipRepository.findAllByFromMemberAndState(member, FriendshipState.INVITED);
    }

    //초대한 친구 요청 확인
    public List<Friendship> getFriendInvitation(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        return friendshipRepository.findAllByFromMemberAndState(member, FriendshipState.INVITED);
    }


    //친구 삭제
    public void removeFriendship(Long fromMemberId, Long toMemberId){
        Friendship friendship = friendshipRepository.findByFromMemberIdAndToMemberId(fromMemberId, toMemberId).orElseThrow(NoSuchElementException::new);
        Member fromMember = friendship.getFromMember();
        Member toMember = friendship.getToMember();
        friendshipRepository.deleteByFromMemberIdAndToMemberId(fromMember.getId(), toMember.getId());
        friendshipRepository.deleteByFromMemberIdAndToMemberId(toMember.getId(), fromMember.getId());
    }
}
