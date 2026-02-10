package com.jiangxia.blog.user.controller;

import com.jiangxia.blog.common.api.ApiResponse;
import com.jiangxia.blog.security.jwt.JwtUtil;
import com.jiangxia.blog.user.dto.LoginRequest;
import com.jiangxia.blog.user.dto.LoginResponse;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.service.UserService;
import com.jiangxia.blog.user.vo.TokenVO;
import com.jiangxia.blog.user.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证模块", description = "用户登录认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @Operation(summary = "公开测试接口", description = "用于测试公开接口是否可访问")
    @GetMapping("/test")
    public String publicTest() {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return "public auth test ok";
    }
}