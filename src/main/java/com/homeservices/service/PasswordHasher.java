package com.homeservices.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordHasher {

    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    static String encode(String plainText) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(plainText, salt, ITERATIONS);
        return "{pbkdf2}$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    static boolean matches(String plainText, String encoded) {
        if (!isEncoded(encoded)) {
            return MessageDigest.isEqual(
                    plainText.getBytes(StandardCharsets.UTF_8),
                    encoded.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = encoded.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(plainText, salt, iterations));
        } catch (RuntimeException ex) {
            return false;
        }
    }

    static boolean isEncoded(String value) {
        return value != null && value.startsWith("{pbkdf2}$");
    }

    private static byte[] derive(String plainText, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt, iterations, KEY_LENGTH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("No fue posible proteger la contraseña.", ex);
        }
    }
}
