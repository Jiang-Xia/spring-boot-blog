package com.jiangxia.blog.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Token响应VO")
@Getter
@Setter
public class TokenVO {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "令牌（兼容旧版本）", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    // Constructors
    public TokenVO() {
    }

    public TokenVO(String accessToken, String refreshToken, String token) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.token = token;
    }
}
