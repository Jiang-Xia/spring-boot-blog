package com.jiangxia.blog.user.controller;

import com.jiangxia.blog.common.api.ApiResponse;
import com.jiangxia.blog.security.jwt.JwtUtil;
import com.jiangxia.blog.user.dto.LoginRequest;
import com.jiangxia.blog.user.dto.LoginResponse;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return ApiResponse.success(new LoginResponse(token));
    }

    @GetMapping("/test")
    public ApiResponse<String> publicTest() {
        return ApiResponse.success("public auth test ok");
    }
}
