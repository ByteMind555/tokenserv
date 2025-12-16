package com.anz.platform.common.tokenserv.repository;

import com.anz.platform.common.tokenserv.model.Token;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

public interface TokenStoreRepository extends CrudRepository<Token, Long> {

    @Query("""
        SELECT TOKEN
        FROM token_store
        WHERE PLAIN_VALUE = :plainValue
          AND EXPIRATION_TIME > :timestamp
        """)
    Optional<String> findNonExpiredToken(
            String plainValue,
            Instant timestamp
    );

    @Query("SELECT PLAIN_VALUE FROM TOKEN_STORE WHERE TOKEN = :token")
    Optional<String> findByToken(String token);

}
