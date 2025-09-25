package com.example.steam.module.member.application;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.domain.MemberGame;
import com.example.steam.module.member.dto.MemberGameDto;
import com.example.steam.module.member.repository.MemberGameRepository;
import com.example.steam.module.product.domain.Product;
import com.example.steam.module.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberGameService {
    private final MemberGameRepository memberGameRepository;
    private final ProductRepository productRepository;

    public MemberGameDto findDtoById(Long memberGameId){
        MemberGame memberGame = memberGameRepository.findById(memberGameId).orElseThrow(NoSuchElementException::new);
        Product product = memberGame.getProduct();
        return MemberGameDto.of(memberGame.getId(), product.getId(), product.getName(), memberGame.getPlayMinutes(), memberGame.getLastPlayedTime(), product.getImageUrl());
    }

    public List<MemberGameDto> getMemberGameDtosByMember(Member member){
        return memberGameRepository.findAllDtoByMember(member);
    }

    public List<MemberGameDto> findTop5DtoByMember(Member member){
        return memberGameRepository.findTop5DtoByMember(member, PageRequest.of(0, 5));
    }

    public boolean isOwned(Member member,Long memberGameId){
        MemberGame memberGame = memberGameRepository.findById(memberGameId).orElseThrow(NoSuchElementException::new);
        Member memberGameMember = memberGame.getMember();
        return memberGameMember.getId().equals(member.getId());
    }
}
