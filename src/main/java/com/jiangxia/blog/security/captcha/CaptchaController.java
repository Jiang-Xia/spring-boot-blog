package com.jiangxia.blog.security.captcha;

import com.jiangxia.blog.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "验证码模块", description = "验证码相关接口")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 生成验证码
     */
    @Operation(summary = "生成验证码", description = "生成验证码图片并返回ID和SVG内容")
    @GetMapping
    public Map<String, String> getCaptcha() {
        CaptchaService.Captcha captcha = captchaService.create();
        Map<String, String> result = new HashMap<>();
        result.put("id", captcha.getId());
        result.put("svg", captcha.getSvg());
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 验证验证码
     */
    @Operation(summary = "验证验证码", description = "验证用户输入的验证码是否正确")
    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestBody VerifyRequest request) {
        boolean ok = captchaService.verify(request.getId(), request.getAnswer());
        Map<String, Boolean> result = new HashMap<>();
        result.put("ok", ok);
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return result;
    }

    /**
     * 验证请求体
     */
    @Getter
    @Setter
    public static class VerifyRequest {
        private String id;
        private String answer;
    }
}