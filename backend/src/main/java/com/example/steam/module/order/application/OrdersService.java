package com.example.steam.module.order.application;

import com.example.steam.core.utils.page.PageConst;
import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.order.domain.Orders;
import com.example.steam.module.order.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final MemberRepository memberRepository;

    //주문 생성
    public Orders makeOrder(Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        return ordersRepository.save(Orders.of(member));
    }

    //회원 주문 조회
    public Page<Orders> getOrdersByMemberId(Long memberId){
        PageRequest pageRequest = PageRequest.of(0, PageConst.ORDERS_PAGE_SIZE);
        return ordersRepository.findAllByMemberIdOrderByCreatedDate(memberId, pageRequest);
    }

    //주문 단건 조회
    public Orders getOrdersById(Long ordersId){
        return ordersRepository.findById(ordersId).orElseThrow(NoSuchElementException::new);
    }

    //주문 취소
    public void removeOrdersById(Long ordersId){
        ordersRepository.deleteById(ordersId);
    }
}
