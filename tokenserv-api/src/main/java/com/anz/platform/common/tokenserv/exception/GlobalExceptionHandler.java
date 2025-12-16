package com.anz.platform.common.tokenserv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            RequestValidationException ex) {

        return ResponseEntity
                .badRequest() // 400
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTokenNotFoundException(
            TokenNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(new ErrorResponse("TOKEN_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<ErrorResponse> handleCryptoException(
            CryptoException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // or 500 if you prefer
                .body(new ErrorResponse("CRYPTO_ERROR", ex.getMessage()));
    }
}
