package com.college.librarybooking.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean verify(String plainPassword, String hash) {
        if (hash == null || hash.isBlank()) {
            return false;
        }
        if (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
            return BCrypt.checkpw(plainPassword, hash);
        }
        return plainPassword.equals(hash);
    }
}
