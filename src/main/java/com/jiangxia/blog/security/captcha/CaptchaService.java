package com.jiangxia.blog.security.captcha;

import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.config.AppConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Service
public class CaptchaService {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CODE_CHARS = "0123456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_TIME = TimeUnit.MINUTES.toSeconds(2); // 2分钟过期
    private static final String DEV_TEST_CODE = "123456"; // 开发测试环境固定验证码
    
    private final RedisTemplate<String, String> redisTemplate;
    private final Random random = new Random();
    private final AppConfig appConfig;
    
    public CaptchaService(RedisTemplate<String, String> redisTemplate, AppConfig appConfig) {
        this.redisTemplate = redisTemplate;
        this.appConfig = appConfig;
    }

    /**
     * 生成验证码
     */
    public Captcha create() {
        String id = UUID.randomUUID().toString().replace("-", "");
        String code = generateCode();
        BufferedImage image = generateImage(code);
        
        // 存储验证码到Redis，有效期2分钟
        String key = CAPTCHA_PREFIX + id;
        redisTemplate.opsForValue().set(key, code.toLowerCase(), CAPTCHA_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return new Captcha(id, code, image);
    }

    /**
     * 验证验证码
     */
    public boolean verify(String id, String code) {
        if (id == null || code == null) {
            return false;
        }
        
        // 开发和测试环境使用固定验证码123456
        if (appConfig.isDevOrTest()) {
            boolean isValid = DEV_TEST_CODE.equals(code);
            if (!isValid) {
                throw new BizException("验证码错误，开发/测试环境请使用: " + DEV_TEST_CODE);
            }
            return true;
        }
        
        // 生产环境进行真实校验
        String key = CAPTCHA_PREFIX + id;
        String expectedCode = redisTemplate.opsForValue().get(key);
        
        if (expectedCode == null) {
            throw new BizException("验证码已过期或不存在");
        }
        
        // 验证后删除（一次性使用）
        redisTemplate.delete(key);
        
        return expectedCode.equalsIgnoreCase(code);
    }

    /**
     * 生成验证码字符串
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片
     */
    private BufferedImage generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 25));
        
        // 绘制验证码
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawString(String.valueOf(code.charAt(i)), 20 + i * 25, 28);
        }
        
        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), 
                       random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        
        g.dispose();
        return image;
    }

    /**
     * 生成SVG格式的验证码（参考blog-server实现）
     */
    private static String generateSvgCaptcha(String code) {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"48\" viewBox=\"0 0 100 48\">");
        svg.append("<rect width=\"100\" height=\"48\" fill=\"transparent\"/>");
        
        // 添加干扰线
        Random rand = new Random();
        for (int i = 0; i < 2; i++) {
            int x1 = rand.nextInt(100);
            int y1 = rand.nextInt(48);
            int x2 = rand.nextInt(100);
            int y2 = rand.nextInt(48);
            svg.append(String.format("<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#%06x\" stroke-width=\"1\"/>", 
                    x1, y1, x2, y2, rand.nextInt(0xFFFFFF)));
        }
        
        // 添加文字
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            int x = 20 + i * 15;
            int y = 30;
            String color = String.format("#%06x", rand.nextInt(0x808080) + 0x404040); // 深色
            svg.append(String.format("<text x=\"%d\" y=\"%d\" font-family=\"Arial\" font-size=\"24\" fill=\"%s\" font-weight=\"bold\">%c</text>", 
                    x, y, color, c));
        }
        
        svg.append("</svg>");
        return svg.toString();
    }

    /**
     * 验证码对象
     */
    public static class Captcha {
        private final String id;
        private final String code;
        private final BufferedImage image;
        private final String svg;

        public Captcha(String id, String code, BufferedImage image) {
            this.id = id;
            this.code = code;
            this.image = image;
            this.svg = generateSvgCaptcha(code);
        }

        public String getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getSvg() {
            return svg;
        }

        // 返回验证码图片
        public BufferedImage getImage() {
            return image;
        }
    }
}
