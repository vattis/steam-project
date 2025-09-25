package com.example.steam.module.member.repository;

import com.example.steam.module.member.domain.Member;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Member> findAllByNicknameContaining(String searchWord, Pageable pageable);

    Page<Member> findById(Long id, Pageable pageable);
}
