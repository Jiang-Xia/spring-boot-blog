package com.jiangxia.blog.common.util;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 对应 Node 端 src/utils/cryptogram.util.ts 中的 makeSalt 和 encryptPassword
 */
public class CryptoUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 16 * 8; // 16 字节 => 128 位

    /**
     * 生成随机盐，对应 makeSalt()
     */
    public static String makeSalt() {
        byte[] salt = new byte[3];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用盐加密明文密码，对应 encryptPassword()
     */
    public static String encryptPassword(String password, String saltBase64) {
        if (password == null || password.isEmpty() || saltBase64 == null || saltBase64.isEmpty()) {
            return "";
        }
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("加密密码失败", e);
        }
    }

    /**
     * 比较密码是否相等，对应 User.compactPass()
     */
    public static boolean matchPassword(String rawPassword, String dbPassword, String saltBase64) {
        String currentHash = encryptPassword(rawPassword, saltBase64);
        return dbPassword != null && dbPassword.equals(currentHash);
    }
}
