package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.AuthorApplyReqDto;
import com.novel.dto.req.UserLoginReqDto;
import com.novel.dto.req.UserRegisterReqDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.dto.resp.UserLoginRespDto;
import com.novel.entity.AuthorApply;
import com.novel.entity.User;
import com.novel.mapper.AuthorApplyMapper;
import com.novel.mapper.UserMapper;
import com.novel.security.JwtTokenProvider;
import com.novel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AuthorApplyMapper authorApplyMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public RestResp<UserLoginRespDto> register(UserRegisterReqDto dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            return RestResp.error("用户名已存在");
        }

        // 创建新用户，默认角色为读者(0)
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 生成 token
        String token = tokenProvider.generateTokenFromUsername(user.getUsername());

        return RestResp.ok(UserLoginRespDto.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build());
    }

    @Override
    public RestResp<UserLoginRespDto> login(UserLoginReqDto dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return RestResp.error("用户不存在");
        }

        if (user.getStatus() != 1) {
            return RestResp.error("账号已被禁用");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return RestResp.error("密码错误");
        }

        String token = tokenProvider.generateTokenFromUsername(user.getUsername());

        return RestResp.ok(UserLoginRespDto.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build());
    }

    @Override
    public RestResp<UserInfoRespDto> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return RestResp.error("用户不存在");
        }

        return RestResp.ok(UserInfoRespDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build());
    }

    @Override
    public RestResp<Void> updateUserInfo(Long userId, String nickname, String email) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return RestResp.error("用户不存在");
        }

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        user.setUpdateTime(LocalDateTime.now());

        userMapper.updateById(user);
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> applyForAuthor(Long userId, AuthorApplyReqDto dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return RestResp.error("用户不存在");
        }

        // 检查是否已经是作者
        if (user.getRole() == 1) {
            return RestResp.error("您已经是作者了");
        }

        // 检查是否有待审核的申请
        LambdaQueryWrapper<AuthorApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthorApply::getUserId, userId)
                .eq(AuthorApply::getStatus, 0);
        if (authorApplyMapper.selectCount(wrapper) > 0) {
            return RestResp.error("您已有待审核的申请，请耐心等待");
        }

        // 创建申请
        AuthorApply apply = new AuthorApply();
        apply.setUserId(userId);
        apply.setRealName(dto.getRealName());
        apply.setIdCard(dto.getIdCard());
        apply.setPhone(dto.getPhone());
        apply.setReason(dto.getReason());
        apply.setStatus(0);
        apply.setCreateTime(LocalDateTime.now());

        authorApplyMapper.insert(apply);
        return RestResp.ok();
    }

    @Override
    public RestResp<AuthorApplyRespDto> getApplyStatus(Long userId) {
        AuthorApply apply = authorApplyMapper.findLatestByUserId(userId);
        if (apply == null) {
            return RestResp.ok(null);
        }

        return RestResp.ok(AuthorApplyRespDto.builder()
                .id(apply.getId())
                .userId(apply.getUserId())
                .realName(apply.getRealName())
                .idCard(apply.getIdCard())
                .phone(apply.getPhone())
                .reason(apply.getReason())
                .status(apply.getStatus())
                .remark(apply.getRemark())
                .createTime(apply.getCreateTime())
                .build());
    }

    @Override
    public RestResp<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return RestResp.error("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return RestResp.error("旧密码错误");
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            return RestResp.error("新密码长度不能少于6位");
        }

        if (newPassword.trim().length() > 100) {
            return RestResp.error("新密码长度不能超过100位");
        }

        user.setPassword(passwordEncoder.encode(newPassword.trim()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        return RestResp.ok();
    }
}