package com.prgrms.ijuju.domain.friend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDto {
    private Long id;
    private String username;
    private String profileImage;
    private boolean isFriend;
    private boolean isActive;
} 