package com.example.steam.module.order.application;

import com.example.steam.module.member.domain.Member;
import com.example.steam.module.member.repository.MemberRepository;
import com.example.steam.module.order.domain.Orders;
import com.example.steam.module.order.repository.OrdersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {
    @InjectMocks OrdersService ordersService;
    @Mock OrdersRepository ordersRepository;
    @Mock MemberRepository memberRepository;

    @Test
    @DisplayName("주문 생성")
    void makeOrder() {
        //given
        Member member = Member.makeSample(1);
        ReflectionTestUtils.setField(member, "id", 1L);
        Orders order = Orders.of(member);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(ordersRepository.save(any(Orders.class))).willReturn(order);

        //when
        ordersService.makeOrder(member.getId());

        //then
        verify(memberRepository, times(1)).findById(member.getId());
        verify(ordersRepository, times(1)).save(any(Orders.class));
    }

    @Test
    @DisplayName("회원의 주문 조회 테스트")
    void getOrdersByMemberId() {
        //given
        Member member = Member.makeSample(1);
        ReflectionTestUtils.setField(member, "id", 1L);

        //when
        ordersService.getOrdersByMemberId(member.getId());

        //then
        verify(ordersRepository, times(1)).findAllByMemberIdOrderByCreatedDate(eq(member.getId()), any(Pageable.class));

    }

    @Test
    @DisplayName("주문 단건 조회 테스트")
    void getOrdersById() {
        //given
        Long ordersId = 1L;
        given(ordersRepository.findById(ordersId)).willReturn(Optional.of(Orders.of(Member.makeSample(1))));

        //when
        ordersService.getOrdersById(ordersId);

        //then
        verify(ordersRepository, times(1)).findById(ordersId);
    }

    @Test
    @DisplayName("주문 삭제 테스트")
    void removeOrdersById() {
        //given
        Long ordersId = 1L;

        //when
        ordersService.removeOrdersById(ordersId);

        //then
        verify(ordersRepository, times(1)).deleteById(ordersId);
    }
}