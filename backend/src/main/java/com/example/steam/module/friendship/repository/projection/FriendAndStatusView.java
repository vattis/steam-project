package com.example.steam.module.friendship.repository.projection;

import com.example.steam.module.friendship.domain.FriendshipState;


public interface FriendAndStatusView {
    Long getToMemberId();
    FriendshipState getFriendshipState();
}
