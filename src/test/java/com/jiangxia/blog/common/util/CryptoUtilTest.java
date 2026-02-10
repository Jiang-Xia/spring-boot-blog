package com.jiangxia.blog.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * CryptoUtil 测试类
 * 验证 AES 加密功能与 blog-server 保持一致
 */
public class CryptoUtilTest {

    @Test
    public void testAesEncryptDecrypt() {
        // 测试数据
        String originalText = "test123";
        String secretKey = "54050000778e380000fe5a120000b4ce";
        String iv = "jiangxia";
        
        // 加密
        String encrypted = CryptoUtil.aesEncrypt(originalText, secretKey, iv);
        System.out.println("加密结果: " + encrypted);
        
        // 解密
        String decrypted = CryptoUtil.aesDecrypt(encrypted, secretKey, iv);
        System.out.println("解密结果: " + decrypted);
        
        // 验证
        assertEquals(originalText, decrypted, "AES加解密应该保持数据一致性");
    }
    
    @Test
    public void testAesEncryptWithDefaultKey() {
        // 使用默认密钥和偏移量
        String originalText = "hello world";
        
        // 加密
        String encrypted = CryptoUtil.aesEncrypt(originalText);
        System.out.println("默认密钥加密结果: " + encrypted);
        
        // 解密
        String decrypted = CryptoUtil.aesDecrypt(encrypted);
        System.out.println("默认密钥解密结果: " + decrypted);
        
        // 验证
        assertEquals(originalText, decrypted, "使用默认密钥的AES加解密应该保持数据一致性");
    }
    
    @Test
    public void testPasswordEncryption() {
        // 测试密码哈希功能
        String password = "123456";
        String salt = CryptoUtil.makeSalt();
        
        String hashedPassword = CryptoUtil.encryptPassword(password, salt);
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
    public void testMakeSalt() {
        String salt1 = CryptoUtil.makeSalt();
        String salt2 = CryptoUtil.makeSalt();
        
        // 验证盐值不为空
        assertNotNull(salt1, "盐值不应该为空");
        assertNotNull(salt2, "盐值不应该为空");
        
        // 验证每次生成的盐值不同
        assertNotEquals(salt1, salt2, "每次生成的盐值应该不同");
        
        System.out.println("盐值1: " + salt1);
        System.out.println("盐值2: " + salt2);
    }
}