package com.example.steam.module.member.repository;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.member.dto.MemberGameDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberGameRepository extends JpaRepository<MemberGame, Long> {
    List<MemberGame> findAllByMember(Member member);
    List<MemberGame> findTop5ByMemberOrderByLastPlayedTimeDesc(Member member);

    @Query("""
        SELECT new com.example.steam.module.member.dto.MemberGameDto(
        mg.id, p.id, p.name, mg.playMinutes, mg.lastPlayedTime, p.imageUrl)
        FROM MemberGame mg 
        JOIN mg.product p
        WHERE mg.member =:member
        ORDER BY p.name DESC
""")
    List<MemberGameDto> findAllDtoByMember(Member member);

    @Query("""
select new com.example.steam.module.member.dto.MemberGameDto(
       mg.id, p.id, p.name, mg.playMinutes, mg.lastPlayedTime, p.imageUrl)
from MemberGame mg
join mg.product p
where mg.member = :member
order by mg.lastPlayedTime desc
""")
    List<MemberGameDto> findTop5DtoByMember(Member member, Pageable pageable);
}
