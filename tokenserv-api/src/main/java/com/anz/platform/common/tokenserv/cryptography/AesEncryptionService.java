package com.anz.platform.common.tokenserv.cryptography;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public final class AesEncryptionService implements CryptoService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12; // bytes (recommended for GCM)

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // prepend IV so it can be used during decryption
        byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    public String decrypt(String encryptedText) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedText);

        byte[] iv = new byte[IV_LENGTH];
        byte[] cipherText = new byte[decoded.length - IV_LENGTH];

        System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
        System.arraycopy(decoded, IV_LENGTH, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, generateKey(), new GCMParameterSpec(GCM_TAG_LENGTH, iv));

        byte[] decrypted = cipher.doFinal(cipherText);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
