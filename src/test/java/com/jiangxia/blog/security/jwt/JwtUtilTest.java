package com.jiangxia.blog.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(1L, "testuser");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.contains("."));
    }

    @Test
    void testParseToken() {
        String token = jwtUtil.generateToken(1L, "testuser");
        
        Claims claims = jwtUtil.parseToken(token);
        
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username"));
        assertEquals(1L, claims.get("uid"));
    }

    @Test
    void testGetUserId() {
        String token = jwtUtil.generateToken(123L, "testuser");
        
        Long userId = jwtUtil.getUserId(token);
        
        assertEquals(123L, userId);
    }

    @Test
    void testGenerateAccessToken() {
        String token = jwtUtil.generateAccessToken(1L, "testuser");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtUtil.generateRefreshToken(1L, "testuser");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}