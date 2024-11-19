package com.prgrms.ijuju.domain.stock.mid.dto.response;

import com.prgrms.ijuju.domain.stock.mid.entity.MidStockTrade;
import com.prgrms.ijuju.domain.stock.mid.entity.TradeType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MidStockTradeResponse(
        long tradePoint,
        long pricePerStock,
        LocalDateTime tradeDate,
        TradeType tradeType,
        String midName
) {
    public static MidStockTradeResponse of (MidStockTrade midStockTrade, String midName) {
        return new MidStockTradeResponse(
                midStockTrade.getTradePoint(),
                midStockTrade.getPricePerStock(),
                midStockTrade.getCreateDate(),
                midStockTrade.getTradeType(),
                midName
        );
    }
}
