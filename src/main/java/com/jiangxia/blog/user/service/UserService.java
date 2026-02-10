package com.jiangxia.blog.user.service;

import com.jiangxia.blog.admin.system.entity.Dept;
import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.admin.system.entity.Role;
import com.jiangxia.blog.admin.system.repository.DeptRepository;
import com.jiangxia.blog.admin.system.repository.PrivilegeRepository;
import com.jiangxia.blog.admin.system.repository.RoleRepository;
import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.common.util.CryptoUtil;
import com.jiangxia.blog.security.email.EmailService;
import com.jiangxia.blog.security.jwt.JwtUtil;
import com.jiangxia.blog.user.dto.*;
import com.jiangxia.blog.user.entity.EmailCodeType;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.entity.UserStatus;
import com.jiangxia.blog.user.repository.UserRepository;
import com.jiangxia.blog.user.vo.TokenVO;
import com.jiangxia.blog.user.vo.UserInfoVO;
import com.jiangxia.blog.user.vo.UserListVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DeptRepository deptRepository;
    private final PrivilegeRepository privilegeRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      DeptRepository deptRepository,
                      PrivilegeRepository privilegeRepository,
                      JwtUtil jwtUtil,
                      EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.deptRepository = deptRepository;
        this.privilegeRepository = privilegeRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    /**
     * 用户注册
     */
    @Transactional
    public UserInfoVO register(RegisterDTO registerDTO) {
        // 检查注册信息
        checkRegisterForm(registerDTO);
        
        // 创建用户
        return createUser(registerDTO);
    }

    /**
     * 检查注册信息
     */
    private void checkRegisterForm(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getPasswordRepeat())) {
            throw new BizException("两次输入的密码不一致，请检查");
        }
        
        String mobile = registerDTO.getMobile();
        Optional<User> hasUser = userRepository.findByMobile(mobile);
        if (hasUser.isPresent()) {
            throw new BizException("账号已存在,建议使用手机号注册");
        }
    }

    /**
     * 创建用户
     */
    private UserInfoVO createUser(RegisterDTO registerDTO) {
        String salt = CryptoUtil.makeSalt();
        String hashedPassword = CryptoUtil.encryptPassword(registerDTO.getPassword(), salt);
        
        User user = new User();
        user.setNickname(registerDTO.getNickname());
        user.setMobile(registerDTO.getMobile());
        user.setUsername(registerDTO.getMobile());
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setAvatar(registerDTO.getAvatar());
        user.setHomepage(registerDTO.getHomepage() != null ? registerDTO.getHomepage() : ""); // 设置默认值为空字符串
        user.setStatus(UserStatus.ACTIVE);
        user.setIsDelete(false);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeptId(4); // 默认部门
        
        // 设置默认角色
        List<Role> roles = roleRepository.findAllById(List.of(3L));
        user.setRoles(new HashSet<>(roles));
        
        User savedUser = userRepository.save(user);
        return UserInfoVO.fromEntity(savedUser);
    }

    /**
     * 用户登录
     */
    @Transactional(readOnly = true)
    public User login(String usernameOrMobileOrEmail, String rawPassword) {
        User user = findByUsernameOrMobileOrEmail(usernameOrMobileOrEmail);
        
        if (Boolean.TRUE.equals(user.getIsDelete())) {
            throw new BizException("用户已被删除");
        }
        
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new BizException("账号已被锁定！");
        }
        
        // 首先尝试智能解密密码（自动识别AES或RSA加密）
        String decryptedPassword = rawPassword;
        try {
            // 使用智能解密处理可能的AES或RSA加密密码
            decryptedPassword = CryptoUtil.smartDecrypt(rawPassword);
            if (!decryptedPassword.equals(rawPassword)) {
                System.out.println("密码解密成功");
            }
        } catch (Exception e) {
            System.out.println("密码解密失败，使用原始密码: " + e.getMessage());
        }
        
        // 使用解密后的密码进行哈希比较
        if (!CryptoUtil.matchPassword(decryptedPassword, user.getPassword(), user.getSalt())) {
            throw new BizException("用户名或密码错误");
        }
        
        return user;
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new BizException("用户不存在"));
    }

    public User findByUsernameOrMobileOrEmail(String usernameOrMobileOrEmail) {
        return userRepository.findByUsername(usernameOrMobileOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrMobileOrEmail))
                .or(() -> userRepository.findByMobile(usernameOrMobileOrEmail))
                .orElseThrow(() -> new BizException("用户不存在"));
    }

    /**
     * 生成 Token
     */
    public TokenVO generateToken(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        
        return new TokenVO(accessToken, refreshToken, token);
    }

    /**
     * 刷新 Token
     */
    public Map<String, Object> refreshToken(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = findById(userId);
            
            TokenVO tokenVO = generateToken(user);
            UserInfoVO userInfoVO = UserInfoVO.fromEntity(user);
            
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", tokenVO.getAccessToken());
            result.put("refreshToken", tokenVO.getRefreshToken());
            result.put("token", tokenVO.getToken());
            result.put("user", userInfoVO);
            result.put("message", "刷新token成功");
            
            return result;
        } catch (Exception e) {
            throw new BizException("token 失效，请重新登录");
        }
    }

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BizException("用户不存在"));
    }

    /**
     * 获取用户信息（包括角色和权限）
     */
    public UserInfoVO getUserInfo(Long userId) {
        User user = findById(userId);
        return UserInfoVO.fromEntity(user);
    }
    
    /**
     * 通过用户名获取用户信息
     */
    public UserInfoVO getUserInfoByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException("用户不存在"));
        return UserInfoVO.fromEntity(user);
    }
    
    /**
     * 获取当前认证用户的信息
     * 从Spring Security上下文中获取当前登录用户
     */
    public UserInfoVO getCurrentUserInfo() {
        // 从Spring Security上下文中获取当前认证用户
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BizException("用户未认证");
        }
            
        // 从认证信息中获取用户名
        String username = authentication.getName();
            
        // 通过用户名获取用户信息
        return getUserInfoByUsername(username);
    }

    /**
     * 获取用户角色权限信息
     */
    public UserInfoVO getUserRolePrivilegeInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        // 使用JOIN FETCH查询加载用户的所有关联数据
        User fullUser = userRepository.findByIdWithRolesAndPrivileges(userId)
                .orElseThrow(() -> new BizException("用户不存在"));

        return UserInfoVO.fromEntity(fullUser);
    }

    /**
     * 获取用户列表
     */
    public UserListVO getUserList(UserListQueryDTO queryDTO) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (queryDTO.getMobile() != null && !queryDTO.getMobile().isEmpty()) {
                predicates.add(cb.like(root.get("mobile"), "%" + queryDTO.getMobile() + "%"));
            }
            
            if (queryDTO.getUsername() != null && !queryDTO.getUsername().isEmpty()) {
                predicates.add(cb.like(root.get("username"), "%" + queryDTO.getUsername() + "%"));
            }
            
            predicates.add(cb.equal(root.get("isDelete"), false));
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Pageable pageable = PageRequest.of(
            queryDTO.getPage() - 1, 
            queryDTO.getPageSize(), 
            Sort.by("createTime").ascending()
        );
        
        Page<User> page = userRepository.findAll(spec, pageable);
        
        List<UserInfoVO> list = page.getContent().stream()
                .map(UserInfoVO::fromEntity)
                .collect(Collectors.toList());
        
        UserListVO.Pagination pagination = new UserListVO.Pagination(
            page.getTotalElements(),
            queryDTO.getPage(),
            queryDTO.getPageSize()
        );
        
        return new UserListVO(list, pagination);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserInfoVO updateUser(UpdateUserDTO updateUserDTO) {
        User user = findById(updateUserDTO.getId());
        
        if (updateUserDTO.getNickname() != null) {
            user.setNickname(updateUserDTO.getNickname());
        }
        if (updateUserDTO.getIntro() != null) {
            user.setIntro(updateUserDTO.getIntro());
        }
        if (updateUserDTO.getAvatar() != null) {
            user.setAvatar(updateUserDTO.getAvatar());
        }
        if (updateUserDTO.getHomepage() != null) {
            user.setHomepage(updateUserDTO.getHomepage());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserInfoVO.fromEntity(savedUser);
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public void updateUserStatus(UpdateUserStatusDTO statusDTO) {
        User user = findById(statusDTO.getId());
        user.setStatus(statusDTO.getStatus());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        if (!updatePasswordDTO.getPassword().equals(updatePasswordDTO.getPasswordRepeat())) {
            throw new BizException("两次输入的密码不一致，请检查");
        }
        
        User user = findById(updatePasswordDTO.getId());
        
        // 验证旧密码
        if (!CryptoUtil.matchPassword(updatePasswordDTO.getPasswordOld(), user.getPassword(), user.getSalt())) {
            throw new BizException("旧密码不正确，请检查！");
        }
        
        // 生成新密码
        String newSalt = CryptoUtil.makeSalt();
        String newHashedPassword = CryptoUtil.encryptPassword(updatePasswordDTO.getPassword(), newSalt);
        
        user.setPassword(newHashedPassword);
        user.setSalt(newSalt);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 重置密码
     */
    @Transactional
    public Map<String, String> resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findByMobile(resetPasswordDTO.getMobile())
                .orElseThrow(() -> new BizException("此用户不存在！"));
        
        if (!user.getNickname().equals(resetPasswordDTO.getNickname())) {
            throw new BizException("用户信息不匹配！");
        }
        
        // 生成默认密码
        String defaultPassword = "123456";
        String newSalt = CryptoUtil.makeSalt();
        String newHashedPassword = CryptoUtil.encryptPassword(defaultPassword, newSalt);
        
        user.setPassword(newHashedPassword);
        user.setSalt(newSalt);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "重置密码成功，默认密码为：123456");
        return result;
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        user.setIsDelete(true);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 发送邮箱验证码
     */
    public Map<String, String> sendEmailCode(SendEmailCodeDTO sendEmailCodeDTO) {
        emailService.checkSendFrequency(sendEmailCodeDTO.getEmail(), sendEmailCodeDTO.getType());
        
        // 根据类型检查邮箱
        if (sendEmailCodeDTO.getType() == EmailCodeType.REGISTER) {
            Optional<User> existUser = userRepository.findByEmail(sendEmailCodeDTO.getEmail());
            if (existUser.isPresent()) {
                throw new BizException("该邮箱已被注册");
            }
        } else if (sendEmailCodeDTO.getType() == EmailCodeType.LOGIN || 
                   sendEmailCodeDTO.getType() == EmailCodeType.RESET) {
            Optional<User> existUser = userRepository.findByEmail(sendEmailCodeDTO.getEmail());
            if (existUser.isEmpty()) {
                throw new BizException("该邮箱尚未注册");
            }
        }
        
        emailService.sendVerificationCode(sendEmailCodeDTO.getEmail(), sendEmailCodeDTO.getType());
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "验证码发送成功");
        return result;
    }

    /**
     * 邮箱注册
     */
    @Transactional
    public UserInfoVO emailRegister(EmailRegisterDTO emailRegisterDTO) {
        // 验证邮箱验证码
        emailService.verifyCode(emailRegisterDTO.getEmail(), 
                              emailRegisterDTO.getVerificationCode(), 
                              "register");
        
        // 检查注册信息
        checkEmailRegisterForm(emailRegisterDTO);
        
        // 创建用户
        String salt = CryptoUtil.makeSalt();
        String hashedPassword = CryptoUtil.encryptPassword(emailRegisterDTO.getPassword(), salt);
        
        User user = new User();
        user.setNickname(emailRegisterDTO.getNickname());
        user.setEmail(emailRegisterDTO.getEmail());
        user.setMobile(""); // 邮箱注册的用户手机号为空
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setAvatar(emailRegisterDTO.getAvatar());
        user.setStatus(UserStatus.ACTIVE);
        user.setIsDelete(false);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return UserInfoVO.fromEntity(savedUser);
    }

    private void checkEmailRegisterForm(EmailRegisterDTO emailRegisterDTO) {
        if (!emailRegisterDTO.getPassword().equals(emailRegisterDTO.getPasswordRepeat())) {
            throw new BizException("两次输入的密码不一致，请检查");
        }
        
        Optional<User> hasUser = userRepository.findByEmail(emailRegisterDTO.getEmail());
        if (hasUser.isPresent()) {
            throw new BizException("该邮箱已被注册");
        }
    }

    /**
     * 邮箱登录
     */
    @Transactional(readOnly = true)
    public User emailLogin(EmailLoginDTO emailLoginDTO) {
        // 验证邮箱验证码
        emailService.verifyCode(emailLoginDTO.getEmail(), 
                              emailLoginDTO.getVerificationCode(), 
                              "login");
        
        User user = userRepository.findByEmail(emailLoginDTO.getEmail())
                .orElseThrow(() -> new BizException("用户不存在"));
        
        if (Boolean.TRUE.equals(user.getIsDelete())) {
            throw new BizException("用户已被删除");
        }
        
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new BizException("账号已被锁定！");
        }
        
        if (!CryptoUtil.matchPassword(emailLoginDTO.getPassword(), user.getPassword(), user.getSalt())) {
            throw new BizException("密码错误");
        }
        
        return user;
    }

    /**
     * 根据 GitHub ID 查找用户
     */
    public Optional<User> findByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId);
    }

    /**
     * 创建或更新 GitHub 用户
     */
    @Transactional
    public User createOrUpdateGithubUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 管理员创建用户
     */
    @Transactional
    public UserInfoVO adminCreateUser(AdminCreateUserDTO adminCreateUserDTO) {
        // 检查账号是否已存在
        Optional<User> existingUser = userRepository.findByUsername(adminCreateUserDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new BizException("该账号已被注册");
        }
        
        String salt = CryptoUtil.makeSalt();
        String hashedPassword = CryptoUtil.encryptPassword(adminCreateUserDTO.getPassword(), salt);
        
        User user = new User();
        user.setNickname(adminCreateUserDTO.getNickname());
        user.setMobile(adminCreateUserDTO.getUsername());
        user.setUsername(adminCreateUserDTO.getUsername());
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setIntro(adminCreateUserDTO.getIntro());
        user.setAvatar(adminCreateUserDTO.getAvatar());
        user.setStatus(UserStatus.ACTIVE);
        user.setIsDelete(false);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 设置部门
        if (adminCreateUserDTO.getDeptId() != null) {
            user.setDeptId(adminCreateUserDTO.getDeptId());
        }
        
        // 设置角色
        if (adminCreateUserDTO.getRoleIds() != null && !adminCreateUserDTO.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(adminCreateUserDTO.getRoleIds());
            if (roles.size() != adminCreateUserDTO.getRoleIds().size()) {
                throw new BizException("部分角色不存在");
            }
            user.setRoles(new HashSet<>(roles));
        }
        
        User savedUser = userRepository.save(user);
        return UserInfoVO.fromEntity(savedUser);
    }

    /**
     * 管理员更新用户
     */
    @Transactional
    public UserInfoVO adminUpdateUser(Long userId, AdminUpdateUserDTO adminUpdateUserDTO) {
        User user = findById(userId);
        
        user.setNickname(adminUpdateUserDTO.getNickname());
        user.setIntro(adminUpdateUserDTO.getIntro());
        user.setAvatar(adminUpdateUserDTO.getAvatar());
        
        // 更新部门
        if (adminUpdateUserDTO.getDeptId() != null) {
            user.setDeptId(adminUpdateUserDTO.getDeptId());
        }
        
        // 更新角色
        if (adminUpdateUserDTO.getRoleIds() != null) {
            if (!adminUpdateUserDTO.getRoleIds().isEmpty()) {
                List<Role> roles = roleRepository.findAllById(adminUpdateUserDTO.getRoleIds());
                if (roles.size() != adminUpdateUserDTO.getRoleIds().size()) {
                    throw new BizException("部分角色不存在");
                }
                user.setRoles(new HashSet<>(roles));
            } else {
                user.setRoles(new HashSet<>());
            }
        }
        
        user.setUpdateTime(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return UserInfoVO.fromEntity(savedUser);
    }
}