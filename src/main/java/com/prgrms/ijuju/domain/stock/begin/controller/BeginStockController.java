package com.prgrms.ijuju.domain.stock.begin.controller;

import com.prgrms.ijuju.domain.stock.begin.dto.response.BeginStockResponse;
import com.prgrms.ijuju.domain.stock.begin.service.BeginStockService;
import com.prgrms.ijuju.global.auth.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/begin-stocks")
@RestController
public class BeginStockController {
    private final BeginStockService beginStockService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BeginStockResponse> getBeginStock(HttpServletRequest request) {
        // JWT 토큰 로깅
        String authHeader = request.getHeader("Authorization");
        log.info("Authorization Header: {}", authHeader);

        // Bearer 토큰만 추출해서 로깅
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("JWT Token: {}", token);
        } else {
            log.warn("No Bearer token found in Authorization header");
        }

        log.info("주식 데이터 요청 컨트롤러 실행");
        BeginStockResponse response = beginStockService.getBeginStockDataWithQuiz();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submissions")
    public ResponseEntity<Void> createBeginQuizResult(@AuthenticationPrincipal SecurityUser user) {
        beginStockService.playBeginStockQuiz(user.getId());
        return ResponseEntity.ok().build();
    }


}
