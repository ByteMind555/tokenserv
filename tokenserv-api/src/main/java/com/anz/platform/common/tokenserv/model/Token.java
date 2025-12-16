package com.anz.platform.common.tokenserv.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;


@Table("TOKEN_STORE")
public record Token(

        @Id
        @Column("TOKEN_ID")
        Long tokenId,

        @Column("PLAIN_VALUE")
        String plainValue,

        @Column("TOKEN")
        String token,

        @Column("CREATION_TIME")
        Instant creationTime,

        @Column("EXPIRATION_TIME")
        Instant expirationTime
) {
}
