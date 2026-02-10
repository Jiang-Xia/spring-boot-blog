package com.jiangxia.blog.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试大写16进制字符串的AES解密
 */
public class UppercaseHexDecryptTest {

    @Test
    public void testUppercaseHexDecryption() {
        // 测试数据
        String originalText = "123456";
        
        // 使用AES加密（结果是大写的16进制字符串）
        String encryptedUppercase = CryptoUtil.aesEncrypt(originalText);
        System.out.println("加密结果（大写）: " + encryptedUppercase);
        
        // 验证确实是大写的
        assertEquals(encryptedUppercase, encryptedUppercase.toUpperCase(), "加密结果应该是大写的");
        
        // 尝试解密大写的16进制字符串
        String decrypted = CryptoUtil.aesDecrypt(encryptedUppercase);
        System.out.println("解密结果: " + decrypted);
        
        // 验证解密正确性
        assertEquals(originalText, decrypted, "大写16进制字符串应该能正确解密");
    }
    
    @Test
    public void testMixedCaseHexHandling() {
        // 测试混合大小写的处理
        String testHex = "ABCDEF0123456789";
        try {
            // 尝试解密一个混合大小写的字符串
            String result = CryptoUtil.aesDecrypt(testHex);
            System.out.println("混合大小写解密结果: " + result);
        } catch (Exception e) {
            System.out.println("混合大小写解密失败（预期）: " + e.getMessage());
        }
    }
    
    @Test
    public void testCharacterDigitFunction() {
        // 验证Character.digit函数能正确处理大写字符
        assertEquals(10, Character.digit('A', 16), "A应该转换为10");
        assertEquals(15, Character.digit('F', 16), "F应该转换为15");
        assertEquals(10, Character.digit('a', 16), "a也应该转换为10");
        assertEquals(15, Character.digit('f', 16), "f也应该转换为15");
        
        System.out.println("Character.digit函数测试通过");
    }
}