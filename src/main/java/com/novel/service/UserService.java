package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.AuthorApplyReqDto;
import com.novel.dto.req.UserLoginReqDto;
import com.novel.dto.req.UserRegisterReqDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.dto.resp.UserLoginRespDto;

public interface UserService {
    RestResp<UserLoginRespDto> register(UserRegisterReqDto dto);
    RestResp<UserLoginRespDto> login(UserLoginReqDto dto);
    RestResp<UserInfoRespDto> getUserInfo(Long userId);
    RestResp<Void> updateUserInfo(Long userId, String nickname, String email);
    RestResp<Void> applyForAuthor(Long userId, AuthorApplyReqDto dto);
    RestResp<AuthorApplyRespDto> getApplyStatus(Long userId);
    RestResp<Void> changePassword(Long userId, String oldPassword, String newPassword);
}