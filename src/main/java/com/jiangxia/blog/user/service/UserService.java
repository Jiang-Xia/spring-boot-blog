package com.jiangxia.blog.user.service;

import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.common.util.CryptoUtil;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BizException("用户不存在"));
    }

    @Transactional(readOnly = true)
    public User login(String usernameOrEmail, String rawPassword) {
        User user = findByUsernameOrEmail(usernameOrEmail);
        if (Boolean.TRUE.equals(user.getIsDelete())) {
            throw new BizException("用户已被删除");
        }
        if (!CryptoUtil.matchPassword(rawPassword, user.getPassword(), user.getSalt())) {
            throw new BizException("用户名或密码错误");
        }
        return user;
    }
}
