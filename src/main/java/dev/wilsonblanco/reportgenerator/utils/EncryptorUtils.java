package dev.wilsonblanco.reportgenerator.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptorUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private final static String MY_SECRET_KEY = "G0MDg4RkdHc566Bi5lNkQxkb0SAgoEz4/yQPPRyXAPc=";

    public static String encrypt(String data) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        byte[] decodedKey = Base64.getDecoder().decode(MY_SECRET_KEY);
        SecretKey key = new SecretKeySpec(decodedKey, ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        byte[] encryptedComplete = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encryptedComplete, 0, iv.length);
        System.arraycopy(cipherText, 0, encryptedComplete, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedComplete);
    }

    public static String decrypt(String encryptedData) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedData);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(decoded, 0, iv, 0, iv.length);

        byte[] cipherText = new byte[decoded.length - iv.length];
        System.arraycopy(decoded, iv.length, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        byte[] decodedKey = Base64.getDecoder().decode(MY_SECRET_KEY);
        SecretKey key = new SecretKeySpec(decodedKey, ALGORITHM);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    public static boolean match(String dataEncrypted, String dataPlain) throws Exception {
        String uncryptedData = decrypt(dataEncrypted);
        return dataPlain.equals(uncryptedData);
    }
}
