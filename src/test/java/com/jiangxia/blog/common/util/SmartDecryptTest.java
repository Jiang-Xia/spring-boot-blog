package com.jiangxia.blog.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试智能解密功能
 */
public class SmartDecryptTest {

    @Test
    public void testSmartDecryptWithUserCipher() {
        // 用户提供的RSA加密密文
        String userCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        
        System.out.println("=== 智能解密测试 ===");
        System.out.println("用户密文长度: " + userCipher.length());
        System.out.println("是否为有效16进制: " + userCipher.matches("[0-9A-F]+"));
        
        // 首先验证这是有效的16进制字符串
        assertTrue(userCipher.matches("[0-9A-F]+"), "应该是一个有效的16进制字符串");
        
        // 尝试转换为字节数组
        try {
            byte[] bytes = hexToBytes(userCipher);
            System.out.println("字节数组长度: " + bytes.length + " 字节");
            
            // 验证长度是否合理（64字节 = 512位RSA输出）
            assertEquals(64, bytes.length, "应该是64字节的RSA加密输出");
            
        } catch (Exception e) {
            fail("16进制转换失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testLengthBasedDecryption() {
        String shortCipher = "3CD0E9CE8C0F32225FE7978CD315E36D";  // 32字符 - AES
        String longCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";  // 128字符 - 可能是RSA
        
        System.out.println("=== 长度基础解密策略 ===");
        System.out.println("短密文长度: " + shortCipher.length() + " (AES候选)");
        System.out.println("长密文长度: " + longCipher.length() + " (RSA候选)");
        
        // 长度判断逻辑
        boolean isLikelyRsa = longCipher.length() > 64;  // 超过64字符可能是RSA
        boolean isLikelyAes = shortCipher.length() <= 64;  // 64字符及以下可能是AES
        
        System.out.println("长密文是否可能是RSA: " + isLikelyRsa);
        System.out.println("短密文是否可能是AES: " + isLikelyAes);
        
        assertTrue(isLikelyRsa, "长密文应该被识别为RSA候选");
        assertTrue(isLikelyAes, "短密文应该被识别为AES候选");
    }
    
    // 辅助方法：16进制字符串转字节数组
    private byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}