package com.translator.util;

import com.translator.security.PasswordEncoder;

public class PasswordGeneratorUtil {
    public static void main(String[] args) {
        // Generate encoded passwords
        System.out.println("admin123: " + PasswordEncoder.encode("admin123"));
        System.out.println("user123: " + PasswordEncoder.encode("user123"));
        System.out.println("test123: " + PasswordEncoder.encode("test123"));
    }
}