package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AdminLoginRespDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.PageRespDto;
import com.novel.dto.resp.UserInfoRespDto;

import java.util.List;
import java.util.Map;

public interface AdminService {
    RestResp<AdminLoginRespDto> adminLogin(String username, String password);

    RestResp<List<UserInfoRespDto>> listAllUsers();
    RestResp<Void> updateUserStatus(Long userId, Integer status);
    RestResp<Void> updateUserRole(Long userId, Integer role);

    RestResp<List<AuthorApplyRespDto>> listAuthorApplies(Integer status);
    RestResp<Void> auditAuthorApply(Long applyId, Integer status, String remark);

    RestResp<PageRespDto<BookInfoRespDto>> listAllBooks(Integer pageNum, Integer pageSize, Integer status);
    RestResp<Void> deleteBook(Long bookId);
    RestResp<Void> offShelfBook(Long bookId);

    RestResp<Void> auditBook(Long bookId, Integer auditStatus, String remark);
    RestResp<PageRespDto<BookInfoRespDto>> getPendingBooks(Integer pageNum, Integer pageSize);

    RestResp<List<CategoryRespDto>> listAllCategories();
    RestResp<Void> addCategory(CategoryAddReqDto dto);
    RestResp<Void> updateCategory(Long categoryId, CategoryAddReqDto dto);
    RestResp<Void> deleteCategory(Long categoryId);

    RestResp<Void> resetPassword(Long adminId, String newPassword);
}