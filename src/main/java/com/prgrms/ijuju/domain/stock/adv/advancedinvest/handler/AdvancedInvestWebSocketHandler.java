package com.prgrms.ijuju.domain.stock.adv.advancedinvest.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.ijuju.common.util.WebSocketUtil;
import com.prgrms.ijuju.domain.stock.adv.advancedinvest.dto.request.WebSocketRequestDto;
import com.prgrms.ijuju.domain.stock.adv.advancedinvest.service.AdvancedInvestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
@RequiredArgsConstructor
public class AdvancedInvestWebSocketHandler extends TextWebSocketHandler {
    private final AdvancedInvestService advancedInvestService;
    private final ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // WebSocketRequestDto 파싱
        WebSocketRequestDto requestDto = objectMapper.readValue(message.getPayload(), WebSocketRequestDto.class);

        // 요청 처리
        switch (requestDto.getAction()) {
            case "START_GAME":
                advancedInvestService.startGame(session, requestDto.getAdvId());
                WebSocketUtil.send(session, "게임이 시작되었습니다.");
                break;

            case "PAUSE_GAME":
                advancedInvestService.pauseGame(requestDto.getAdvId(), advancedInvestService.getRemainingTime(requestDto.getAdvId()));
                WebSocketUtil.send(session, "게임이 일시정지되었습니다.");
                break;

            case "RESUME_GAME":
                advancedInvestService.resumeGame(session, requestDto.getAdvId());
                WebSocketUtil.send(session, "게임이 재개되었습니다.");
                break;

            case "END_GAME":
                advancedInvestService.endGame(requestDto.getAdvId());
                WebSocketUtil.send(session, "게임이 종료되었습니다.");
                break;

            case "GET_REMAINING_TIME":
                int remainingTime = advancedInvestService.getRemainingTime(requestDto.getAdvId());
                WebSocketUtil.send(session, "남은 시간: " + remainingTime + "초");
                break;

            default:
                WebSocketUtil.send(session, "알 수 없는 액션입니다: " + requestDto.getAction());
        }
    }
}
