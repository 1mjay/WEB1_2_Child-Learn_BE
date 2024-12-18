package com.prgrms.ijuju.domain.ranking.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RankingResponse {
    private final long rank;
    private final String username;
    private final long weeklyPoints;

    @Builder
    public RankingResponse(long rank, String username, long weeklyPoints) {
        this.rank = rank;
        this.username = username;
        this.weeklyPoints = weeklyPoints;
    }

    public static RankingResponse of(long rank, String username, long weeklyPoints) {
        return RankingResponse.builder()
                .rank(rank)
                .username(username)
                .weeklyPoints(weeklyPoints)
                .build();
    }
}
