package com.anz.platform.common.tokenserv.service;

import com.anz.platform.common.tokenserv.cryptography.CryptoService;
import com.anz.platform.common.tokenserv.exception.CryptoException;
import com.anz.platform.common.tokenserv.exception.RequestValidationException;
import com.anz.platform.common.tokenserv.model.Token;
import com.anz.platform.common.tokenserv.model.TokenizerRequest;
import com.anz.platform.common.tokenserv.model.TokenizerResponse;
import com.anz.platform.common.tokenserv.repository.TokenStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServServiceImplTest {

    private TokenStoreRepository repository;
    private CryptoService cryptoService;
    private TokenServServiceImpl service;

    @BeforeEach
    void setup() {
        repository = mock(TokenStoreRepository.class);
        cryptoService = mock(CryptoService.class);
        service = new TokenServServiceImpl(repository, cryptoService);
    }

    @Test
    void tokenise_newToken_encryptsAndSaves() throws CryptoException, Exception, RequestValidationException {
        String plainValue = "4111-1111-1111-1111";
        String encryptedToken = "encryptedValue";

        when(repository.findNonExpiredToken(eq(plainValue), any())).thenReturn(Optional.empty());
        when(cryptoService.encrypt(plainValue)).thenReturn(encryptedToken);

        TokenizerResponse response = service.tokenise(new TokenizerRequest(List.of(plainValue)));

        assertEquals(1, response.getEntity().size());
        assertEquals(encryptedToken, response.getEntity().get(0));

        // Capture what was saved
        ArgumentCaptor<List<Token>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<Token> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertEquals(plainValue, saved.get(0).plainValue());
        assertEquals(encryptedToken, saved.get(0).token());
    }

    @Test
    void tokenise_nullOrEmptyRequest_throws() {
        assertThrows(RequestValidationException.class, () -> service.tokenise(null));
        assertThrows(RequestValidationException.class, () -> service.tokenise(new TokenizerRequest(List.of())));
    }

    @Test
    void deTokenise_existingToken_returnsPlainValue() throws RequestValidationException {
        String token = "tokenValue";
        String plainValue = "4111-1111-1111-1111";

        when(repository.findByToken(token)).thenReturn(Optional.of(plainValue));

        TokenizerResponse response = service.deTokenise(new TokenizerRequest(List.of(token)));

        assertEquals(1, response.getEntity().size());
        assertEquals(plainValue, response.getEntity().get(0));
    }

    @Test
    void deTokenise_missingToken_returnsErrorMessage() throws RequestValidationException {
        String token = "unknownToken";

        when(repository.findByToken(token)).thenReturn(Optional.empty());

        TokenizerResponse response = service.deTokenise(new TokenizerRequest(List.of(token)));

        assertEquals(1, response.getEntity().size());
        assertTrue(response.getEntity().get(0).contains("No entry found"));
    }

    @Test
    void deTokenise_nullOrEmptyRequest_throws() {
        assertThrows(RequestValidationException.class, () -> service.deTokenise(null));
        assertThrows(RequestValidationException.class, () -> service.deTokenise(new TokenizerRequest(List.of())));
    }

    //@Test
    void tokenise_existingToken_returnsSameToken() throws CryptoException, Exception, RequestValidationException {
        String plainValue = "4111-1111-1111-1111";
        String existingToken = "existingEncrypted";

        when(repository.findNonExpiredToken(eq(plainValue), any())).thenReturn(Optional.of(existingToken));

        TokenizerResponse response = service.tokenise(new TokenizerRequest(List.of(plainValue)));

        assertEquals(1, response.getEntity().size());
        assertEquals(existingToken, response.getEntity().get(0));

        // Ensure no encryption or save is called
        verify(cryptoService, never()).encrypt(any());
        verify(repository, never()).saveAll(any());
    }
}
