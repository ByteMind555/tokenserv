package com.anz.platform.common.tokenserv.controller;

import com.anz.platform.common.tokenserv.exception.CryptoException;
import com.anz.platform.common.tokenserv.exception.RequestValidationException;
import com.anz.platform.common.tokenserv.model.TokenizerRequest;
import com.anz.platform.common.tokenserv.model.TokenizerResponse;
import com.anz.platform.common.tokenserv.service.TokenServService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tokenserv/api/v1/")
public class TokenServController {

    @Autowired
    private TokenServService tokenServService;

    @PostMapping("/tokenize")
    public TokenizerResponse tokenise(@RequestBody TokenizerRequest tokenizerRequest) {
        try {
            TokenizerResponse tokenise = tokenServService.tokenise(tokenizerRequest);
            return tokenise;
        }
        catch (CryptoException ex) {
            return null;
        }
        catch (RequestValidationException e) {
            return null;
        }
    }

    @PostMapping("/detokenize")
    public TokenizerResponse deTokenise(@RequestBody TokenizerRequest tokenizerRequest) {
        try {
            TokenizerResponse deTokenise = tokenServService.deTokenise(tokenizerRequest);
            return deTokenise;
        }
        catch (RequestValidationException e)
        {
            return null;
        }
    }
}
