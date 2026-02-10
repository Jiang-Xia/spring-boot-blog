package com.jiangxia.blog.user.service;

import com.jiangxia.blog.common.util.CryptoUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单的密码解密测试（不依赖Spring上下文）
 */
public class SimplePasswordDecryptTest {

    @Test
    public void testAesEncryptionDecryption() {
        // 测试AES加解密功能
        String originalText = "123456";
        String encrypted = CryptoUtil.aesEncrypt(originalText);
        String decrypted = CryptoUtil.aesDecrypt(encrypted);
        
        System.out.println("原始文本: " + originalText);
        System.out.println("加密结果: " + encrypted);
        System.out.println("解密结果: " + decrypted);
        
        assertEquals(originalText, decrypted, "AES加解密应该保持一致性");
    }
    
    @Test
    public void testPasswordHashWithSalt() {
        // 测试密码哈希功能
        String password = "123456";
        String salt = CryptoUtil.makeSalt();
        String hashedPassword = CryptoUtil.encryptPassword(password, salt);
        
        System.out.println("密码: " + password);
        System.out.println("盐值: " + salt);
        System.out.println("哈希密码: " + hashedPassword);
        
        // 验证密码匹配
        boolean matches = CryptoUtil.matchPassword(password, hashedPassword, salt);
        assertTrue(matches, "相同密码应该匹配");
        
        // 验证错误密码不匹配
        boolean notMatches = CryptoUtil.matchPassword("wrongpassword", hashedPassword, salt);
        assertFalse(notMatches, "不同密码不应该匹配");
    }
    
    @Test
    public void testDecryptAttemptWithPlainPassword() {
        // 测试对未加密密码进行解密的情况
        String plainPassword = "123456";
        
        try {
            String decrypted = CryptoUtil.aesDecrypt(plainPassword);
            System.out.println("意外解密成功: " + decrypted);
            // 如果解密成功，应该等于原密码
            assertEquals(plainPassword, decrypted);
        } catch (Exception e) {
            System.out.println("解密失败（预期行为）: " + e.getMessage());
            // 这是预期的行为 - 未加密的密码无法解密
        }
    }
}