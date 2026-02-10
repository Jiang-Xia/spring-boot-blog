package com.jiangxia.blog.user.service;

import com.jiangxia.blog.common.util.CryptoUtil;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserService 密码解密测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserServicePasswordDecryptTest {

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testPasswordWithAesDecrypt() {
        // 测试数据
        String originalPassword = "123456";
        String encryptedPassword = CryptoUtil.aesEncrypt(originalPassword);
        
        System.out.println("原始密码: " + originalPassword);
        System.out.println("加密密码: " + encryptedPassword);
        
        // 验证加密解密一致性
        String decryptedPassword = CryptoUtil.aesDecrypt(encryptedPassword);
        assertEquals(originalPassword, decryptedPassword, "AES加解密应该保持一致性");
        
        System.out.println("解密密码: " + decryptedPassword);
    }
    
    @Test
    public void testPasswordWithoutEncryption() {
        // 测试未加密的密码
        String plainPassword = "123456";
        
        // 应该能够处理未加密的密码（解密会失败，但程序应该继续使用原始密码）
        try {
            String decrypted = CryptoUtil.aesDecrypt(plainPassword);
            System.out.println("意外解密成功: " + decrypted);
        } catch (Exception e) {
            System.out.println("解密失败，这是预期的: " + e.getMessage());
            // 这是预期的行为
        }
    }
}