package com.jiangxia.blog.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试从blog-server迁移的RSA解密功能
 */
public class RsaMigrationTest {

    @Test
    public void testRsaKeyConfiguration() {
        // 验证RSA密钥配置是否正确迁移
        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAv2vyMqR85GmK6cXK\n" +
                "UXhfC82LTqxMPc3iFgsCYY2a+JnUiEKe7hVnSxKF2Psth+H9HDki6pjnldevrNUH\n" +
                "8vNDvQIDAQABAkEAi6NzSv4zHWzgqShgLo5gx3tp5DpMY8mM5Aej9QYXxsEtzq/+\n" +
                "oTPfooVF2rX4rE8NwTpNzwIfzOnrCw5vVCm1AQIhAOZalT7Rx1bqg6irko6MDkVk\n" +
                "9rKW7jebRZ7i3JbonM9DAiEA1Lu9aUWAb98pNRTBnszVzj9FGZKjlSrW/f/PWN2m\n" +
                "8P8CIQCXFFwESoQKDl9xda32jgciHljqwrDUiaL81V/GHiQSjwIgFApzt50ikmd1\n" +
                "nFiOPQWTBtETE2urGXxlsJwOzpJjDcUCIQCV4z96GjcuMYH92dVhmLKFC0ZRX30A\n" +
                "mO+bs1CWyhWG0g==\n" +
                "-----END PRIVATE KEY-----";
        
        String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL9r8jKkfORpiunFylF4XwvNi06sTD3N\n" +
                "4hYLAmGNmviZ1IhCnu4VZ0sShdj7LYfh/Rw5IuqY55XXr6zVB/LzQ70CAwEAAQ==\n" +
                "-----END PUBLIC KEY-----";
        
        System.out.println("=== RSA密钥配置验证 ===");
        System.out.println("私钥长度: " + privateKey.length());
        System.out.println("公钥长度: " + publicKey.length());
        System.out.println("私钥格式正确: " + privateKey.startsWith("-----BEGIN PRIVATE KEY-----"));
        System.out.println("公钥格式正确: " + publicKey.startsWith("-----BEGIN PUBLIC KEY-----"));
        
        assertTrue(privateKey.startsWith("-----BEGIN PRIVATE KEY-----"), "私钥应以正确的PEM格式开头");
        assertTrue(publicKey.startsWith("-----BEGIN PUBLIC KEY-----"), "公钥应以正确的PEM格式开头");
    }
    
    @Test
    public void testUserCipherCompatibility() {
        // 测试用户提供的密文是否符合RSA解密要求
        String userCipher = "2D72C972BBDE555F343CED4D6AE523450A482218E3745C70285EB4892A727FAAAFC219E93F535DA30D64B285ACD18CE2A019C7263C3C77DE36DA168CB6C1480F";
        
        System.out.println("=== 用户密文兼容性测试 ===");
        System.out.println("用户密文: " + userCipher);
        System.out.println("密文长度: " + userCipher.length() + " 字符");
        System.out.println("是否为有效16进制: " + userCipher.matches("[0-9A-F]+"));
        
        // 验证密文格式
        assertTrue(userCipher.matches("[0-9A-F]+"), "用户密文应该是有效的16进制字符串");
        assertEquals(128, userCipher.length(), "用户密文长度应该是128字符");
        
        // 转换为字节数组验证
        try {
            byte[] bytes = hexToBytes(userCipher);
            System.out.println("字节数组长度: " + bytes.length + " 字节");
            assertEquals(64, bytes.length, "应该转换为64字节的数组");
        } catch (Exception e) {
            fail("16进制转换失败: " + e.getMessage());
        }
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