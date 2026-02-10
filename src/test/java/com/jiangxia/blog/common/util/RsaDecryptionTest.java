package com.jiangxia.blog.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试RSA加密解密可能性
 */
public class RsaDecryptionTest {

    @Test
    public void testRsaEncryptedPassword() {
        // 用户提供的密文（可能是RSA加密）
        String userCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        
        System.out.println("=== RSA加密分析 ===");
        System.out.println("用户密文长度: " + userCipher.length() + " 字符");
        System.out.println("是否为有效16进制: " + userCipher.matches("[0-9A-F]+"));
        
        // RSA加密通常产生较长的输出
        // 1024位RSA密钥产生128字节输出 = 256个16进制字符
        // 2048位RSA密钥产生256字节输出 = 512个16进制字符
        
        System.out.println("密文长度分析:");
        System.out.println("- 128字符 = 64字节，可能是512位RSA");
        System.out.println("- 256字符 = 128字节，可能是1024位RSA");
        System.out.println("- 512字符 = 256字节，可能是2048位RSA");
        
        // 尝试将密文转换为Base64（RSA通常输出Base64）
        try {
            // 将16进制转换为字节数组
            byte[] bytes = hexToBytes(userCipher);
            System.out.println("字节数组长度: " + bytes.length + " 字节");
            
            // 尝试作为Base64解码
            String base64String = java.util.Base64.getEncoder().encodeToString(bytes);
            System.out.println("Base64编码结果长度: " + base64String.length());
            
        } catch (Exception e) {
            System.out.println("转换失败: " + e.getMessage());
        }
    }
    
    @Test 
    public void testAesVsRsaLengthComparison() {
        String testPassword = "123456";
        
        System.out.println("=== AES vs RSA 长度对比 ===");
        
        // AES加密结果
        String aesResult = CryptoUtil.aesEncrypt(testPassword);
        System.out.println("AES加密结果: " + aesResult);
        System.out.println("AES长度: " + aesResult.length() + " 字符 (" + (aesResult.length()/2) + " 字节)");
        
        // 模拟RSA加密长度（通常比AES长很多）
        System.out.println("\n常见RSA密钥长度对应的输出:");
        System.out.println("512位RSA: 128字节 = 256个16进制字符");
        System.out.println("1024位RSA: 128字节 = 256个16进制字符"); 
        System.out.println("2048位RSA: 256字节 = 512个16进制字符");
        
        // 用户密文长度
        String userCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        System.out.println("\n用户密文长度: " + userCipher.length() + " 字符 (" + (userCipher.length()/2) + " 字节)");
        System.out.println("用户密文长度匹配: " + (userCipher.length() == 128 ? "可能是512位RSA" : "其他"));
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