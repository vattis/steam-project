package com.example.steam.module.friendship.domain;

public enum FriendshipState {
    INVITE_SENT, // 내가 초대 보냈고, 수락 대기
    INVITED,    //내가 초대를 받는 친구 관계
    FRIENDS,     // 초대가 수락되어 친구 관계 성립
    NONE         // 아무 관계 없음 (초대도 안 보냄)
}