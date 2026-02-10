package com.jiangxia.blog.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 比较前端和后端AES加密结果
 */
public class FrontendBackendComparisonTest {

    @Test
    public void compareEncryptionResults() {
        String testPassword = "123456";
        
        // 前端使用的参数（来自blog-admin/src/utils/crypto.ts）
        String frontendKey = "54050000778e380000fe5a120000b4ce";
        String frontendIV = "jiangxia";
        
        // 我们后端使用的相同参数
        String backendKey = "54050000778e380000fe5a120000b4ce";
        String backendIV = "jiangxia";  // 我们的填充后长度不符
        
        System.out.println("=== 加密参数对比 ===");
        System.out.println("密钥长度 - 前端: " + frontendKey.length() + ", 后端: " + backendKey.length());
        System.out.println("IV长度 - 前端: " + frontendIV.length() + ", 后端: " + backendIV.length());
        
        // 使用相同的参数进行加密
        String backendResult = CryptoUtil.aesEncrypt(testPassword, backendKey, backendIV);
        System.out.println("后端加密结果: " + backendResult);
        System.out.println("后端结果长度: " + backendResult.length() + " 字符");
        
        // 分析用户提供的密文
        String userCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        System.out.println("用户密文长度: " + userCipher.length() + " 字符");
        System.out.println("用户密文: " + userCipher);
        
        // 尝试不同的IV长度
        System.out.println("\n=== 尝试不同的IV处理方式 ===");
        tryDifferentIVApproaches(testPassword, frontendKey, frontendIV, userCipher);
    }
    
    private void tryDifferentIVApproaches(String password, String key, String iv, String userCipher) {
        // 尝试不同的IV处理方式
        String[] ivVariations = {
            iv,                                    // 原始8字符
            iv + "jiangx",                        // 补齐到16字符
            "jiangxiaj",                          // 16字符
            "1234567812345678"                    // 16字符标准
        };
        
        for (int i = 0; i < ivVariations.length; i++) {
            String testIV = ivVariations[i];
            System.out.println("\n尝试IV变体 " + (i+1) + ": " + testIV + " (长度: " + testIV.length() + ")");
            
            try {
                String encrypted = CryptoUtil.aesEncrypt(password, key, testIV);
                System.out.println("  加密结果: " + encrypted);
                System.out.println("  长度: " + encrypted.length());
                
                // 尝试解密用户密文
                try {
                    String decrypted = CryptoUtil.aesDecrypt(userCipher, key, testIV);
                    System.out.println("  ✓ 用户密文解密成功: " + decrypted);
                    return; // 找到正确配置
                } catch (Exception e) {
                    System.out.println("  ✗ 用户密文解密失败: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("  加密失败: " + e.getMessage());
            }
        }
    }
    
    @Test
    public void analyzeUserCipherStructure() {
        String userCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        
        // 检查是否可以分割为块
        System.out.println("=== 密文结构分析 ===");
        System.out.println("总长度: " + userCipher.length());
        
        // 尝试按不同块大小分割
        int[] blockSizes = {32, 16, 8};
        for (int blockSize : blockSizes) {
            if (userCipher.length() % blockSize == 0) {
                int numBlocks = userCipher.length() / blockSize;
                System.out.println("可以分割为 " + blockSize + " 字符的块，共 " + numBlocks + " 块");
                
                // 显示前几个块
                for (int i = 0; i < Math.min(3, numBlocks); i++) {
                    String block = userCipher.substring(i * blockSize, (i + 1) * blockSize);
                    System.out.println("  块 " + i + ": " + block);
                }
            }
        }
    }
}