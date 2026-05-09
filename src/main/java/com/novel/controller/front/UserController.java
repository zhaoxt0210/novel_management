<<<<<<< HEAD
package com.novel.controller.front;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.AuthorApplyReqDto;
import com.novel.dto.req.UserLoginReqDto;
import com.novel.dto.req.UserRegisterReqDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.dto.resp.UserLoginRespDto;
import com.novel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户模块", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public RestResp<UserLoginRespDto> register(@Valid @RequestBody UserRegisterReqDto dto) {
        return userService.register(dto);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public RestResp<UserLoginRespDto> login(@Valid @RequestBody UserLoginReqDto dto) {
        return userService.login(dto);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info/{userId}")
    public RestResp<UserInfoRespDto> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info/{userId}")
    public RestResp<Void> updateUserInfo(@PathVariable Long userId,
                                         @RequestParam(required = false) String nickname,
                                         @RequestParam(required = false) String email) {
        return userService.updateUserInfo(userId, nickname, email);
    }

    @Operation(summary = "申请成为作者")
    @PostMapping("/apply-author/{userId}")
    public RestResp<Void> applyForAuthor(@PathVariable Long userId, 
                                          @Valid @RequestBody AuthorApplyReqDto dto) {
        return userService.applyForAuthor(userId, dto);
    }

    @Operation(summary = "查询作者申请状态")
    @GetMapping("/apply-status/{userId}")
    public RestResp<AuthorApplyRespDto> getApplyStatus(@PathVariable Long userId) {
        return userService.getApplyStatus(userId);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/change-password/{userId}")
    public RestResp<Void> changePassword(@PathVariable Long userId,
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        return userService.changePassword(userId, oldPassword, newPassword);
    }
=======
package com.novel.controller.front;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.AuthorApplyReqDto;
import com.novel.dto.req.UserLoginReqDto;
import com.novel.dto.req.UserRegisterReqDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.dto.resp.UserLoginRespDto;
import com.novel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户模块", description = "用户相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public RestResp<UserLoginRespDto> register(@Valid @RequestBody UserRegisterReqDto dto) {
        return userService.register(dto);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public RestResp<UserLoginRespDto> login(@Valid @RequestBody UserLoginReqDto dto) {
        return userService.login(dto);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info/{userId}")
    public RestResp<UserInfoRespDto> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info/{userId}")
    public RestResp<Void> updateUserInfo(@PathVariable Long userId,
                                         @RequestParam(required = false) String nickname,
                                         @RequestParam(required = false) String email) {
        return userService.updateUserInfo(userId, nickname, email);
    }

    @Operation(summary = "申请成为作者")
    @PostMapping("/apply-author/{userId}")
    public RestResp<Void> applyForAuthor(@PathVariable Long userId, 
                                          @Valid @RequestBody AuthorApplyReqDto dto) {
        return userService.applyForAuthor(userId, dto);
    }

    @Operation(summary = "查询作者申请状态")
    @GetMapping("/apply-status/{userId}")
    public RestResp<AuthorApplyRespDto> getApplyStatus(@PathVariable Long userId) {
        return userService.getApplyStatus(userId);
    }
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}