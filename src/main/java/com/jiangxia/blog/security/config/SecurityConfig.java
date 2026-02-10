package com.jiangxia.blog.security.config;

import com.jiangxia.blog.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 公开接口
                        .requestMatchers( "/auth/test", "/pub/**").permitAll()
                        .requestMatchers("/user/register", "/user/login", "/user/authCode", "/user/refresh", "/user/email/sendCode", "/user/email/register", "/user/email/login").permitAll()
                        .requestMatchers("/captcha/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/article/list", "/article/info", "/article/views", "/article/likes").permitAll()
                        
                        // 管理员专用接口
//                        .requestMatchers("/user/admin/**").hasRole("ADMIN")
                        
                        // 用户相关接口（需要认证）
                        .requestMatchers("/user/info", "/user/edit", "/user/password", "/user/resetPassword").authenticated()
                        
                        // 文章管理接口（需要认证）
                        .requestMatchers("/article/create", "/article/edit", "/article/delete", "/article/disabled", "/article/topping").authenticated()
                        
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
