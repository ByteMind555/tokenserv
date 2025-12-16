package com.anz.platform.common.tokenserv.service;

import com.anz.platform.common.tokenserv.exception.CryptoException;
import com.anz.platform.common.tokenserv.exception.RequestValidationException;
import com.anz.platform.common.tokenserv.exception.TokenNotFoundException;
import com.anz.platform.common.tokenserv.model.TokenizerRequest;
import com.anz.platform.common.tokenserv.model.TokenizerResponse;

public interface TokenServService {

    TokenizerResponse tokenise(TokenizerRequest tokenizerRequest) throws CryptoException, RequestValidationException;

    TokenizerResponse deTokenise(TokenizerRequest tokenizerRequest) throws RequestValidationException, TokenNotFoundException;

}
