package com.jiangxia.blog.user.controller;

import com.jiangxia.blog.common.api.ApiResponse;
import com.jiangxia.blog.security.captcha.CaptchaService;
import com.jiangxia.blog.user.dto.*;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.service.UserService;
import com.jiangxia.blog.user.vo.TokenVO;
import com.jiangxia.blog.user.vo.UserInfoVO;
import com.jiangxia.blog.user.vo.UserListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户模块", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CaptchaService captchaService;

    public UserController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    /**
     * 从Cookie中获取验证码ID
     */
    private String getCaptchaIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("captcha_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取验证码
     */
    @Operation(summary = "获取验证码", description = "生成验证码图片并设置ID到cookie")
    @GetMapping("/authCode")
    public ResponseEntity<byte[]> createCaptcha(HttpServletResponse response) throws IOException {
        CaptchaService.Captcha captcha = captchaService.create();
        
        // 将验证码ID设置到Cookie中，方便后续验证
        response.addHeader("Set-Cookie", "captcha_id=" + captcha.getId() + 
                          "; HttpOnly; Path=/; SameSite=Strict; Max-Age=120"); // 2分钟有效期
        
        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store");
        
        // 将图片转换为字节数组
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(captcha.getImage(), "png", baos);
        byte[] imageBytes = baos.toByteArray();
        
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.IMAGE_PNG)
                .header("Cache-Control", "no-cache, no-store")
                .body(imageBytes);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "账号注册", description = "通过手机号和验证码注册账号")
    @PostMapping("/register")
    public UserInfoVO register(@Valid @RequestBody RegisterDTO registerDTO, 
                              HttpServletRequest httpRequest) {
        // 从Cookie中获取验证码ID
        String captchaId = getCaptchaIdFromCookie(httpRequest);
        
        // 验证验证码
        captchaService.verify(captchaId, registerDTO.getAuthCode());
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.register(registerDTO);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "账号登录", description = "通过用户名、手机号或邮箱和密码登录")
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request, 
                                    HttpServletRequest httpRequest) {
        // 从Cookie中获取验证码ID
        String captchaId = getCaptchaIdFromCookie(httpRequest);
        
        // 验证验证码
        captchaService.verify(captchaId, request.getAuthCode());
        
        // 根据请求参数决定使用哪个字段登录
        String loginCredential;
        if (request.getMobile() != null && !request.getMobile().isEmpty()) {
            loginCredential = request.getMobile();
        } else {
            loginCredential = request.getUsername();
        }
        
        User user = userService.login(loginCredential, request.getPassword());
        TokenVO tokenVO = userService.generateToken(user);
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> info = new HashMap<>();
        info.put("token", tokenVO.getToken());
        info.put("accessToken", tokenVO.getAccessToken());
        info.put("refreshToken", tokenVO.getRefreshToken());
        info.put("user", UserInfoVO.fromEntity(user));
        result.put("info", info);
        
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 刷新 Token
     */
    @Operation(summary = "刷新token", description = "使用刷新token获取新的访问token")
    @GetMapping("/refresh")
    public Map<String, Object> refresh(@RequestParam("token") String token) {
        Map<String, Object> result = userService.refreshToken(token);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/info")
    public UserInfoVO userInfo() {
        // 使用封装的方法获取当前用户信息
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.getCurrentUserInfo();
    }

    /**
     * 获取用户列表
     */
    @Operation(summary = "获取用户列表", description = "获取用户列表，支持分页和搜索")
    @PostMapping("/list")
    public UserListVO getUserList(@RequestBody UserListQueryDTO queryDTO) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.getUserList(queryDTO);
    }

    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态", description = "更新用户的锁定/激活状态")
    @PatchMapping("/status")
    public Boolean updateStatus(@RequestBody UpdateUserStatusDTO statusDTO) {
        userService.updateUserStatus(statusDTO);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return true;
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息", description = "更新用户的基本信息")
    @PatchMapping("/edit")
    public UserInfoVO updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.updateUser(updateUserDTO);
    }

    /**
     * 修改密码
     */
    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @PatchMapping("/password")
    public Boolean updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        userService.updatePassword(updatePasswordDTO);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return true;
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码", description = "通过手机号和昵称重置密码")
    @PostMapping("/resetPassword")
    public Map<String, String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        Map<String, String> result = userService.resetPassword(resetPasswordDTO);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "软删除用户")
    @DeleteMapping
    public Boolean deleteById(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return true;
    }

    /**
     * 发送邮箱验证码
     */
    @Operation(summary = "发送邮箱验证码", description = "发送邮箱验证码")
    @PostMapping("/email/sendCode")
    public Map<String, String> sendEmailCode(@Valid @RequestBody SendEmailCodeDTO sendEmailCodeDTO) {
        Map<String, String> result = userService.sendEmailCode(sendEmailCodeDTO);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 邮箱注册
     */
    @Operation(summary = "邮箱注册", description = "通过邮箱和验证码注册账号")
    @PostMapping("/email/register")
    public UserInfoVO emailRegister(@Valid @RequestBody EmailRegisterDTO emailRegisterDTO) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.emailRegister(emailRegisterDTO);
    }

    /**
     * 邮箱登录
     */
    @Operation(summary = "邮箱登录", description = "通过邮箱和验证码登录账号")
    @PostMapping("/email/login")
    public Map<String, Object> emailLogin(@Valid @RequestBody EmailLoginDTO emailLoginDTO) {
        User user = userService.emailLogin(emailLoginDTO);
        TokenVO tokenVO = userService.generateToken(user);
        
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> info = new HashMap<>();
        info.put("token", tokenVO.getToken());
        info.put("accessToken", tokenVO.getAccessToken());
        info.put("refreshToken", tokenVO.getRefreshToken());
        info.put("user", UserInfoVO.fromEntity(user));
        result.put("info", info);
        
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 管理员创建用户
     */
    @Operation(summary = "管理员创建用户", description = "仅限超级管理员创建用户并绑定角色和部门")
    @PostMapping("/admin/create")
    public UserInfoVO adminCreateUser(@Valid @RequestBody AdminCreateUserDTO adminCreateUserDTO) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.adminCreateUser(adminCreateUserDTO);
    }

    /**
     * 管理员更新用户
     */
    @Operation(summary = "管理员更新用户", description = "仅限超级管理员更新用户信息并绑定角色和部门（手机号和用户名不可修改）")
    @PostMapping("/admin/update/{id}")
    public UserInfoVO adminUpdateUser(@PathVariable("id") Long id, 
                                     @Valid @RequestBody AdminUpdateUserDTO adminUpdateUserDTO) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return userService.adminUpdateUser(id, adminUpdateUserDTO);
    }
}