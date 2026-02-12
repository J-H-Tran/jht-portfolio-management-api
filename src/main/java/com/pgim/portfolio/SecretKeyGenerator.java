package com.pgim.portfolio;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        // Generate a random 32-byte key
        byte[] key = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);

        // Encode the key in Base64 for use as a string
        String secretKey = Base64.getEncoder().encodeToString(key);

        System.out.println("Generated Secret Key: " + secretKey);
    }
}
