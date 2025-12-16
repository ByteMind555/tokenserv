package com.anz.platform.common.tokenserv.controller;

import com.anz.platform.common.tokenserv.exception.CryptoException;
import com.anz.platform.common.tokenserv.exception.RequestValidationException;
import com.anz.platform.common.tokenserv.exception.TokenNotFoundException;
import com.anz.platform.common.tokenserv.model.TokenizerRequest;
import com.anz.platform.common.tokenserv.model.TokenizerResponse;
import com.anz.platform.common.tokenserv.service.TokenServService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/tokens")
@Tag(
        name = "Token Service",
        description = "APIs for tokenizing and detokenizing sensitive data"
)
public class TokenServController {

    @Autowired
    private TokenServService tokenServService;


    @Operation(
            summary = "Tokenize sensitive data",
            description = "Accepts raw sensitive data and returns a secure token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenizerResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or validation failure",
                    content = @Content
            )
    })
    @PostMapping("/tokenize")
    public ResponseEntity<TokenizerResponse> tokenise(@RequestBody TokenizerRequest tokenizerRequest) throws CryptoException, RequestValidationException {
        TokenizerResponse tokenise = tokenServService.tokenise(tokenizerRequest);
        return ResponseEntity.ok(tokenise);
    }

    @Operation(
            summary = "Detokenize a token",
            description = "Accepts a token and returns the original sensitive data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detokenization successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenizerResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid token or validation error",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Token not found",
                    content = @Content
            )
    })
    @PostMapping("/detokenize")
    public ResponseEntity<TokenizerResponse> deTokenise(@RequestBody TokenizerRequest tokenizerRequest) throws RequestValidationException, TokenNotFoundException {
        TokenizerResponse deTokenise = tokenServService.deTokenise(tokenizerRequest);
        return ResponseEntity.ok(deTokenise);
    }
}
