package com.mentoree.common.encrpyt.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import static com.mentoree.common.encrpyt.constant.SecurityConstant.*;

public class AESUtils implements EncryptUtils {

    private final String secretKey = ENCRYPT_KEY.getValue();

    private static final int IV_LENGTH = 16;
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Override
    public String encrypt(String message) {
        try {
            // Key spec
            byte[] byteMessage = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(byteMessage, ALGORITHM);

            // IV spec with SecureRandom
            byte[] ivBytes = generateRandomIV();
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            //encrypt message
            Cipher c = Cipher.getInstance(CIPHER_TRANSFORMATION);
            c.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = c.doFinal(message.getBytes(StandardCharsets.UTF_8));

            //concat iv + encrypted message
            byte[] resultByte = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(ivBytes, 0, resultByte, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, resultByte, IV_LENGTH, encrypted.length);
            return Base64.getEncoder().encode(resultByte).toString();

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Error cause : " + e.getMessage());
        }
    }

    @Override
    public String decrypt(String message) {
        try {
            byte[] byteMessage = Base64.getDecoder().decode(message);

            //extract iv
            byte[] ivBytes = Arrays.copyOfRange(byteMessage, 0, IV_LENGTH);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            byte[] encryptKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(encryptKeyBytes, ALGORITHM);

            //decrypt message
            Cipher c = Cipher.getInstance(CIPHER_TRANSFORMATION);
            c.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            //extract encrypted message
            byte[] encryptMessage = Arrays.copyOfRange(byteMessage, IV_LENGTH, byteMessage.length);

            return new String(c.doFinal(encryptMessage));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Error cause : " + e.getMessage());
        }
    }

    private byte[] generateRandomIV() {
        SecureRandom random = new SecureRandom();
        byte[] ivByte = new byte[IV_LENGTH];
        random.nextBytes(ivByte);
        return ivByte;
    }
}
