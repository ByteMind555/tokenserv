package com.anz.platform.common.tokenserv.service;

import com.anz.platform.common.tokenserv.cryptography.CryptoService;
import com.anz.platform.common.tokenserv.exception.CryptoException;
import com.anz.platform.common.tokenserv.exception.RequestValidationException;
import com.anz.platform.common.tokenserv.model.Token;
import com.anz.platform.common.tokenserv.model.TokenizerRequest;
import com.anz.platform.common.tokenserv.model.TokenizerResponse;
import com.anz.platform.common.tokenserv.repository.TokenStoreRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TokenServServiceImpl implements TokenServService {

    private static final long TOKEN_TTL_SECONDS = 300;

    private final TokenStoreRepository tokenStoreRepository;
    private final CryptoService cryptoService;

    public TokenServServiceImpl(TokenStoreRepository tokenStoreRepository,
                                CryptoService cryptoService) {
        this.tokenStoreRepository = tokenStoreRepository;
        this.cryptoService = cryptoService;
    }

    @Override
    public TokenizerResponse tokenise(TokenizerRequest request)
            throws CryptoException, RequestValidationException {
        // Step 1: Validate
        validateRequest(request);
        // Step 2: Tokenize by checking DB
        return tokenizeEntities(request.getEntity());
    }

    @Override
    public TokenizerResponse deTokenise(TokenizerRequest request)
            throws RequestValidationException {

        validateRequest(request);
        List<String> response = new ArrayList<>();
        for (String token : request.getEntity()) {
            response.add(resolvePlainValue(token));
        }
        return new TokenizerResponse(response);
    }

    private TokenizerResponse tokenizeEntities(List<String> plainValues)
            throws CryptoException {

        List<Token> tokensToPersist = new ArrayList<>();
        List<String> responseTokens = new ArrayList<>();
        Instant now = Instant.now();
        for (String plainValue : plainValues) {
            responseTokens.add(
                    getOrCreateToken(plainValue, now, tokensToPersist)
            );
        }
        tokenStoreRepository.saveAll(tokensToPersist);
        return new TokenizerResponse(responseTokens);
    }

    private String getOrCreateToken(String plainValue,
                                    Instant now,
                                    List<Token> tokensToPersist)
            throws CryptoException {

        Optional<String> existingToken =
                tokenStoreRepository.findNonExpiredToken(plainValue, now);

        if (existingToken.isPresent()) {
            return existingToken.get();
        }

        return createAndCollectToken(plainValue, now, tokensToPersist);
    }

    private String createAndCollectToken(String plainValue,
                                         Instant now,
                                         List<Token> tokensToPersist)
            throws CryptoException {

        try {
            String encryptedToken = cryptoService.encrypt(plainValue);

            tokensToPersist.add(new Token(
                    null,
                    plainValue,
                    encryptedToken,
                    now,
                    now.plusSeconds(TOKEN_TTL_SECONDS)
            ));

            return encryptedToken;
        } catch (Exception e) {
            throw new CryptoException(
                    "Unexpected exception during encryption of: " + plainValue, e
            );
        }
    }


    private String resolvePlainValue(String token) {
        return tokenStoreRepository.findByToken(token)
                .orElse("No entry found for token: " + token);
    }

    private void validateRequest(TokenizerRequest request) throws RequestValidationException {
        if (request == null || request.getEntity() == null || request.getEntity().isEmpty()) {
            throw new RequestValidationException(
                    "Request is null or empty. Please pass a valid request."
            );
        }
    }
}
