package com.cist2931.petstore.application;


import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHelper {
    private PasswordHelper() {}

    private static final int workload = 12;
    public static final String HASH_PREFIX = "$2a$" + workload + "$";

    public static String hashPassword(String plaintextPassword) {
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(plaintextPassword, salt);
    }

    public static boolean verifyPassword(String plaintextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith(HASH_PREFIX)) {
            // TODO: Log (invalid hash provided for comparison)
            return false;
        }
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }
}
