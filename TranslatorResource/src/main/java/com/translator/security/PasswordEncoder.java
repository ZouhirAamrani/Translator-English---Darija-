package com.translator.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Encode password with salt
     */
    public static String encode(String password) {
        try {
            // Generate salt
            byte[] salt = generateSalt();
            
            // Hash password with salt
            byte[] hash = hash(password, salt);
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            
            // Encode to Base64
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password encoding failed", e);
        }
    }
    
    /**
     * Verify password against encoded hash
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(encodedPassword);
            
            // Extract salt and hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] storedHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, storedHash, 0, storedHash.length);
            
            // Hash the raw password with extracted salt
            byte[] computedHash = hash(rawPassword, salt);
            
            // Compare hashes
            return MessageDigest.isEqual(storedHash, computedHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    private static byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.update(salt);
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}