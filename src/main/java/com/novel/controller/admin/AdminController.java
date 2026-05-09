package com.novel.controller.admin;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AdminLoginRespDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "管理员模块", description = "管理员后台接口")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public RestResp<AdminLoginRespDto> adminLogin(@RequestParam String username, @RequestParam String password) {
        return adminService.adminLogin(username, password);
    }

    // ========== 用户管理 ==========
    @Operation(summary = "获取所有用户")
    @GetMapping("/users")
    public RestResp<List<UserInfoRespDto>> listAllUsers() {
        return adminService.listAllUsers();
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/users/{userId}/status")
    public RestResp<Void> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        return adminService.updateUserStatus(userId, status);
    }

    @Operation(summary = "更新用户角色")
    @PutMapping("/users/{userId}/role")
    public RestResp<Void> updateUserRole(@PathVariable Long userId, @RequestParam Integer role) {
        return adminService.updateUserRole(userId, role);
    }

    // ========== 作者审核 ==========
    @Operation(summary = "获取作者申请列表")
    @GetMapping("/author-applies")
    public RestResp<List<AuthorApplyRespDto>> listAuthorApplies(@RequestParam(required = false) Integer status) {
        return adminService.listAuthorApplies(status);
    }

    @Operation(summary = "审核作者申请")
    @PutMapping("/author-applies/{applyId}/audit")
    public RestResp<Void> auditAuthorApply(@PathVariable Long applyId,
                                           @RequestParam Integer status,
                                           @RequestParam(required = false) String remark) {
        return adminService.auditAuthorApply(applyId, status, remark);
    }

    // ========== 小说管理 ==========
    @Operation(summary = "获取所有小说")
    @GetMapping("/books")
    public RestResp<List<BookInfoRespDto>> listAllBooks() {
        return adminService.listAllBooks();
    }

    @Operation(summary = "删除小说")
    @DeleteMapping("/books/{bookId}")
    public RestResp<Void> deleteBook(@PathVariable Long bookId) {
        return adminService.deleteBook(bookId);
    }

    @Operation(summary = "下架小说")
    @PutMapping("/books/{bookId}/off-shelf")
    public RestResp<Void> offShelfBook(@PathVariable Long bookId) {
        return adminService.offShelfBook(bookId);
    }

    // ========== 作品审核 ==========
    @Operation(summary = "审核作品")
    @PutMapping("/books/{bookId}/audit")
    public RestResp<Void> auditBook(@PathVariable Long bookId,
                                    @RequestParam Integer auditStatus,
                                    @RequestParam(required = false) String remark) {
        return adminService.auditBook(bookId, auditStatus, remark);
    }

    @Operation(summary = "获取待审核作品列表")
    @GetMapping("/books/pending")
    public RestResp<List<BookInfoRespDto>> getPendingBooks() {
        return adminService.getPendingBooks();
    }

    // ========== 分类管理 ==========
    @Operation(summary = "获取所有分类")
    @GetMapping("/categories")
    public RestResp<List<CategoryRespDto>> listAllCategories() {
        return adminService.listAllCategories();
    }

    @Operation(summary = "添加分类")
    @PostMapping("/categories")
    public RestResp<Void> addCategory(@Valid @RequestBody CategoryAddReqDto dto) {
        return adminService.addCategory(dto);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/categories/{categoryId}")
    public RestResp<Void> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryAddReqDto dto) {
        return adminService.updateCategory(categoryId, dto);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/categories/{categoryId}")
    public RestResp<Void> deleteCategory(@PathVariable Long categoryId) {
        return adminService.deleteCategory(categoryId);
    }

    // ========== 统计数据 ==========
    @Operation(summary = "获取统计数据")
    @GetMapping("/statistics")
    public RestResp<Map<String, Object>> getStatistics() {
        return adminService.getStatistics();
    }

    // ========== 密码重置 ==========
    @Operation(summary = "重置管理员密码")
    @PutMapping("/{adminId}/password")
    public RestResp<Void> resetPassword(@PathVariable Long adminId, @RequestParam String newPassword) {
        return adminService.resetPassword(adminId, newPassword);
    }
}