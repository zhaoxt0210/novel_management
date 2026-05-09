<<<<<<< HEAD
package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AdminLoginRespDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import java.util.List;

public interface AdminService {
    RestResp<AdminLoginRespDto> adminLogin(String username, String password);

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

    // 作品审核
    RestResp<Void> auditBook(Long bookId, Integer auditStatus, String remark);
    RestResp<List<BookInfoRespDto>> getPendingBooks();

    // 分类管理
    RestResp<List<CategoryRespDto>> listAllCategories();
    RestResp<Void> addCategory(CategoryAddReqDto dto);
    RestResp<Void> updateCategory(Long categoryId, CategoryAddReqDto dto);
    RestResp<Void> deleteCategory(Long categoryId);

    RestResp<Void> resetPassword(Long adminId, String newPassword);
=======
package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AdminLoginRespDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import java.util.List;

public interface AdminService {
    RestResp<AdminLoginRespDto> adminLogin(String username, String password);

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

    // 作品审核
    RestResp<Void> auditBook(Long bookId, Integer auditStatus, String remark);
    RestResp<List<BookInfoRespDto>> getPendingBooks();

    // 分类管理
    RestResp<List<CategoryRespDto>> listAllCategories();
    RestResp<Void> addCategory(CategoryAddReqDto dto);
    RestResp<Void> updateCategory(Long categoryId, CategoryAddReqDto dto);
    RestResp<Void> deleteCategory(Long categoryId);

    RestResp<Void> resetPassword(Long adminId, String newPassword);
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}