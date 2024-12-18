package com.prgrms.ijuju.global.exception;

import com.prgrms.ijuju.domain.minigame.flipcard.dto.response.ErrorFlipCardResponse;
import com.prgrms.ijuju.domain.minigame.flipcard.exception.FlipCardErrorCode;
import com.prgrms.ijuju.domain.minigame.flipcard.exception.FlipCardException;
import com.prgrms.ijuju.domain.stock.mid.dto.response.ErrorMidResponse;
import com.prgrms.ijuju.domain.stock.mid.exception.MidStockErrorCode;
import com.prgrms.ijuju.domain.stock.mid.exception.MidStockException;
import com.prgrms.ijuju.global.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MidStockException.class)
    protected ResponseEntity<ErrorMidResponse> handleMidStockException(final MidStockException e) {
        log.error("MidStockException: {}", e.getMessage());
        final MidStockErrorCode errorCode = e.getErrorCode();
        final ErrorMidResponse response = ErrorMidResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(FlipCardException.class)
    protected ResponseEntity<ErrorFlipCardResponse> handleFlipCardException(final FlipCardException e) {
        log.error("FlipCardException: {}", e.getMessage());
        final FlipCardErrorCode errorCode = e.getErrorCode();
        final ErrorFlipCardResponse response = ErrorFlipCardResponse.of(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // @ExceptionHandler(WalletException.class)
    // public ResponseEntity<ErrorResponse> handleWalletException(WalletException e) {
    //     log.error("WalletException: {}", e.getMessage());

    //     ErrorResponse response = ErrorResponse.builder()
    //             .code(e.getCode())
    //             .message(e.getMessage())
    //             .status(e.getHttpStatus().value())
    //             .build();

    //     return new ResponseEntity<>(response, e.getHttpStatus());
    // }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .code("SERVER_ERROR")
                .message("서버 오류가 발생했습니다.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
