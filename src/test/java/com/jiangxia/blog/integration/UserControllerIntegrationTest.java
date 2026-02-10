package com.jiangxia.blog.integration;

import com.jiangxia.blog.BlogApplication;
import com.jiangxia.blog.security.jwt.JwtUtil;
import com.jiangxia.blog.user.dto.LoginRequest;
import com.jiangxia.blog.user.dto.RegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BlogApplication.class)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testRegisterEndpoint() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setNickname("Test User");
        registerDTO.setMobile("13800138000");
        registerDTO.setPassword("password123");
        registerDTO.setPasswordRepeat("password123");
        registerDTO.setAuthCode("123456"); // 验证码在开发环境下是固定的

        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"Test User\",\"mobile\":\"13800138000\",\"password\":\"password123\",\"passwordRepeat\":\"password123\",\"authCode\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testLoginEndpoint() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        loginRequest.setAuthCode("123456");

        mockMvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\",\"authCode\":\"123456\"}"))
                 .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testPublicEndpoints() throws Exception {
        mockMvc.perform(post("/article/list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"page\":1,\"pageSize\":10,\"client\":true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}