package com.jiangxia.blog.common.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对应 Node 端 src/utils/cryptogram.util.ts 中的 makeSalt 和 encryptPassword
 * 同时支持 AES 对称加密，与 blog-server 保持一致
 */
public class CryptoUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 16 * 8; // 16 字节 => 128 位
    
    // AES 加密配置 - 与 blog-server 保持一致
    private static final String AES_SECRET_KEY = "54050000778e380000fe5a120000b4ce"; // 32位密钥
    private static final String AES_IV = "jiangxia"; // 16位偏移量，补齐到16字节
    
    // RSA 密钥配置 - 从 blog-server 迁移过来
    private static final String RSA_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAv2vyMqR85GmK6cXK\n" +
            "UXhfC82LTqxMPc3iFgsCYY2a+JnUiEKe7hVnSxKF2Psth+H9HDki6pjnldevrNUH\n" +
            "8vNDvQIDAQABAkEAi6NzSv4zHWzgqShgLo5gx3tp5DpMY8mM5Aej9QYXxsEtzq/+\n" +
            "oTPfooVF2rX4rE8NwTpNzwIfzOnrCw5vVCm1AQIhAOZalT7Rx1bqg6irko6MDkVk\n" +
            "9rKW7jebRZ7i3JbonM9DAiEA1Lu9aUWAb98pNRTBnszVzj9FGZKjlSrW/f/PWN2m\n" +
            "8P8CIQCXFFwESoQKDl9xda32jgciHljqwrDUiaL81V/GHiQSjwIgFApzt50ikmd1\n" +
            "nFiOPQWTBtETE2urGXxlsJwOzpJjDcUCIQCV4z96GjcuMYH92dVhmLKFC0ZRX30A\n" +
            "mO+bs1CWyhWG0g==\n" +
            "-----END PRIVATE KEY-----";
            
    private static final String RSA_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL9r8jKkfORpiunFylF4XwvNi06sTD3N\n" +
            "4hYLAmGNmviZ1IhCnu4VZ0sShdj7LYfh/Rw5IuqY55XXr6zVB/LzQ70CAwEAAQ==\n" +
            "-----END PUBLIC KEY-----";

    /**
     * 生成随机盐，对应 makeSalt()
     */
    public static String makeSalt() {
        byte[] salt = new byte[3];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 生成随机盐（别名）
     */
    public static String generateSalt() {
        return makeSalt();
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
    
    /**
     * AES加密 - 与 blog-server 保持一致
     * @param word 需要加密的明文
     * @return 16进制大写字符串
     */
    public static String aesEncrypt(String word) {
        return aesEncrypt(word, AES_SECRET_KEY, AES_IV);
    }
    
    /**
     * AES加密
     * @param word 需要加密的明文
     * @param key 密钥
     * @param iv 偏移量
     * @return 16进制大写字符串
     */
    public static String aesEncrypt(String word, String key, String iv) {
        try {
            // 将密钥和偏移量都转换为16字节
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] key16 = new byte[16];
            System.arraycopy(keyBytes, 0, key16, 0, Math.min(keyBytes.length, 16));
            
            byte[] ivBytes = iv.getBytes("UTF-8");
            byte[] iv16 = new byte[16];
            System.arraycopy(ivBytes, 0, iv16, 0, Math.min(ivBytes.length, 16));
            
            SecretKeySpec secretKey = new SecretKeySpec(key16, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv16);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            
            byte[] encrypted = cipher.doFinal(word.getBytes("UTF-8"));
            return bytesToHex(encrypted).toUpperCase();
        } catch (Exception e) {
            throw new IllegalStateException("AES加密失败", e);
        }
    }
    
    /**
     * AES解密 - 与 blog-server 保持一致
     * @param encryptedWord 16进制加密字符串
     * @return 解密后的明文
     */
    public static String aesDecrypt(String encryptedWord) {
        return aesDecrypt(encryptedWord, AES_SECRET_KEY, AES_IV);
    }
    
    /**
     * AES解密
     * @param encryptedWord 16进制加密字符串
     * @param key 密钥
     * @param iv 偏移量
     * @return 解密后的明文
     */
    public static String aesDecrypt(String encryptedWord, String key, String iv) {
        try {
            // 将密钥和偏移量都转换为16字节
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] key16 = new byte[16];
            System.arraycopy(keyBytes, 0, key16, 0, Math.min(keyBytes.length, 16));
            
            byte[] ivBytes = iv.getBytes("UTF-8");
            byte[] iv16 = new byte[16];
            System.arraycopy(ivBytes, 0, iv16, 0, Math.min(ivBytes.length, 16));
            
            SecretKeySpec secretKey = new SecretKeySpec(key16, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv16);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            byte[] encryptedBytes = hexToBytes(encryptedWord);
            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException("AES解密失败", e);
        }
    }
    
    /**
     * RSA解密 - 与 blog-server 保持一致的实现
     * @param encryptedPassword 16进制格式的RSA加密密码
     * @return 解密后的明文密码
     */
    public static String rsaDecrypt(String encryptedPassword) {
        try {
            // 将16进制字符串转换为Base64字符串
            // 这与blog-server中的 enc.Base64.stringify(enc.Hex.parse(encryptedWord)) 对应
            byte[] hexBytes = hexToBytes(encryptedPassword);
            String base64String = Base64.getEncoder().encodeToString(hexBytes);
            
            // 解析私钥（去除PEM格式的头部和尾部）
            String privateKeyPEM = RSA_PRIVATE_KEY
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(spec);
            
            // 使用RSA解密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64String));
            return new String(decryptedBytes, "UTF-8");
            
        } catch (Exception e) {
            throw new IllegalStateException("RSA解密失败", e);
        }
    }
    
    /**
     * 智能解密 - 自动识别并处理AES或RSA加密的密码
     * @param encryptedPassword 可能是AES或RSA加密的密码
     * @return 解密后的明文密码
     */
    public static String smartDecrypt(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isEmpty()) {
            return encryptedPassword;
        }
        
        try {
            // 首先尝试RSA解密（针对长密文，如128字符的用户密文）
            if (encryptedPassword.length() > 64) {  // RSA加密通常产生更长的输出
                return rsaDecrypt(encryptedPassword);
            } else {
                // 尝试AES解密（针对短密文）
                return aesDecrypt(encryptedPassword);
            }
        } catch (Exception e) {
            // 如果解密失败，返回原始字符串
            System.out.println("解密失败，使用原始密码: " + e.getMessage());
            return encryptedPassword;
        }
    }
    
    /**
     * 字节数组转16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * 16进制字符串转字节数组
     */
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
