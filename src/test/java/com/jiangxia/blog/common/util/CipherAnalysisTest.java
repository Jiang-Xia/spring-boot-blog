package com.jiangxia.blog.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 分析用户提供的密文问题
 */
public class CipherAnalysisTest {

    @Test
    public void analyzeProvidedCipherText() {
        // 用户提供的密文
        String userCipherText = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        
        System.out.println("用户密文长度: " + userCipherText.length() + " 字符");
        System.out.println("用户密文: " + userCipherText);
        
        // 检查是否为有效的16进制字符串
        boolean isValidHex = userCipherText.matches("[0-9A-F]+");
        System.out.println("是否为有效大写16进制: " + isValidHex);
        
        // 计算字节数
        int byteLength = userCipherText.length() / 2;
        System.out.println("对应的字节数: " + byteLength + " 字节");
        
        // AES加密的预期长度分析
        String testText = "123456";
        String ourCipherText = CryptoUtil.aesEncrypt(testText);
        System.out.println("我们的加密结果长度: " + ourCipherText.length() + " 字符");
        System.out.println("我们的加密结果: " + ourCipherText);
        System.out.println("我们的字节数: " + (ourCipherText.length() / 2) + " 字节");
        
        // 尝试解密用户提供的密文
        try {
            String decrypted = CryptoUtil.aesDecrypt(userCipherText);
            System.out.println("用户密文解密成功: " + decrypted);
        } catch (Exception e) {
            System.out.println("用户密文解密失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Test
    public void testDifferentKeyLengths() {
        // 测试不同长度的密钥
        String shortKey = "short";
        String longKey = "thisisaverylongkeythatis32bytes!";
        
        try {
            String encrypted1 = CryptoUtil.aesEncrypt("test", shortKey, "jiangxia");
            System.out.println("短密钥加密: " + encrypted1);
        } catch (Exception e) {
            System.out.println("短密钥加密失败: " + e.getMessage());
        }
        
        try {
            String encrypted2 = CryptoUtil.aesEncrypt("test", longKey, "jiangxia");
            System.out.println("长密钥加密: " + encrypted2);
        } catch (Exception e) {
            System.out.println("长密钥加密失败: " + e.getMessage());
        }
    }
}