package com.anz.platform.common.tokenserv.cryptography;

import javax.crypto.SecretKey;

public interface CryptoService {

    public String encrypt(String plainText) throws Exception;

    public String decrypt(String encryptedText) throws Exception;

}
