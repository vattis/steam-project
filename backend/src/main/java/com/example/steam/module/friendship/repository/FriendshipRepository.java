package com.example.steam.module.friendship.repository;

import com.example.steam.module.friendship.domain.Friendship;
import com.example.steam.module.friendship.domain.FriendshipState;
import com.example.steam.module.friendship.repository.projection.FriendAndStatusView;
import com.example.steam.module.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    public List<Friendship> findAllByFromMemberId(Long fromMemberId);
    public List<Friendship> findAllByToMemberAndState(Member toMember, FriendshipState state);
    public List<Friendship> findAllByFromMemberAndState(Member fromMember, FriendshipState friendshipState);
    Page<Friendship> findAllByFromMember_IdAndState(Long fromMemberId, FriendshipState friendshipState, Pageable pageable);
    public boolean existsByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);
    public Optional<Friendship> findByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);
    public void deleteByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

    @Query("""
        select f.toMember.id as toMemberId, f.state as friendshipState
        from Friendship f
        where f.fromMember.id = :loginMemberId
        and f.toMember.id in :friendIds
""")
    List<FriendAndStatusView> findFriendIdAndStatus(Long loginMemberId, List<Long> friendIds);
}
