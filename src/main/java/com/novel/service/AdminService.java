package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import java.util.List;
import java.util.Map;

public interface AdminService {
    RestResp<Void> adminLogin(String username, String password);
    
    // 用户管理
    RestResp<List<UserInfoRespDto>> listAllUsers();
    RestResp<Void> updateUserStatus(Long userId, Integer status);
    RestResp<Void> updateUserRole(Long userId, Integer role);
    
    // 作者审核
    RestResp<List<AuthorApplyRespDto>> listAuthorApplies(Integer status);
    RestResp<Void> auditAuthorApply(Long applyId, Integer status, String remark);
    
    // 小说管理
    RestResp<List<BookInfoRespDto>> listAllBooks();
    RestResp<Void> deleteBook(Long bookId);
    RestResp<Void> offShelfBook(Long bookId);
    
    // 分类管理
    RestResp<List<CategoryRespDto>> listAllCategories();
    RestResp<Void> addCategory(CategoryAddReqDto dto);
    RestResp<Void> updateCategory(Long categoryId, CategoryAddReqDto dto);
    RestResp<Void> deleteCategory(Long categoryId);
    
    // 统计
    RestResp<Map<String, Object>> getStatistics();
}