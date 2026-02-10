package com.jiangxia.blog.user.service;

import com.jiangxia.blog.admin.system.repository.RoleRepository;
import com.jiangxia.blog.admin.system.repository.DeptRepository;
import com.jiangxia.blog.admin.system.repository.PrivilegeRepository;
import com.jiangxia.blog.security.email.EmailService;
import com.jiangxia.blog.security.jwt.JwtUtil;
import com.jiangxia.blog.user.dto.RegisterDTO;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.entity.UserStatus;
import com.jiangxia.blog.user.repository.UserRepository;
import com.jiangxia.blog.user.vo.UserInfoVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.jiangxia.blog.common.util.CryptoUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DeptRepository deptRepository;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private CryptoUtil cryptoUtil;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUserSuccessfully() {
        // 准备测试数据
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNickname("Test User");
        registerDTO.setMobile("13800138000");
        registerDTO.setPassword("password123");

        // 模拟依赖项行为
        when(userRepository.findByMobile("13800138000")).thenReturn(Optional.empty());
        when(cryptoUtil.makeSalt()).thenReturn("test_salt");
        when(cryptoUtil.encryptPassword("password123", "test_salt")).thenReturn("encrypted_password");

        // 执行测试
        UserInfoVO result = userService.register(registerDTO);

        // 验证结果
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindUserById() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNickname("Test User");
        mockUser.setStatus(UserStatus.ACTIVE);

        // 模拟依赖项行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // 执行测试
        User result = userService.findById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("Test User", result.getNickname());
        assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testFindUserByIdNotFound() {
        // 模拟依赖项行为
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            userService.findById(999L);
        });
    }
}