package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.entity.*;
import com.novel.mapper.*;
import com.novel.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;
    private final AuthorApplyMapper authorApplyMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RestResp<Void> adminLogin(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            return RestResp.error("用户名或密码错误");
        }
        if (admin.getStatus() != 1) {
            return RestResp.error("账号已被禁用");
        }
        return RestResp.ok();
    }

    @Override
    public RestResp<List<UserInfoRespDto>> listAllUsers() {
        List<User> users = userMapper.selectList(null);
        return RestResp.ok(users.stream().map(this::convertToUserDto).collect(Collectors.toList()));
    }

    @Override
    public RestResp<Void> updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        userMapper.updateById(user);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> updateUserRole(Long userId, Integer role) {
        User user = new User();
        user.setId(userId);
        user.setRole(role);
        userMapper.updateById(user);
        return RestResp.ok();
    }

    @Override
    public RestResp<List<AuthorApplyRespDto>> listAuthorApplies(Integer status) {
        LambdaQueryWrapper<AuthorApply> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(AuthorApply::getStatus, status);
        }
        wrapper.orderByDesc(AuthorApply::getCreateTime);
        List<AuthorApply> applies = authorApplyMapper.selectList(wrapper);
        
        if (applies.isEmpty()) {
            return RestResp.ok(List.of());
        }
        
        List<Long> userIds = applies.stream()
                .map(AuthorApply::getUserId)
                .collect(Collectors.toList());
        
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        return RestResp.ok(applies.stream().map(apply -> {
            User user = userMap.get(apply.getUserId());
            return AuthorApplyRespDto.builder()
                    .id(apply.getId())
                    .userId(apply.getUserId())
                    .username(user != null ? user.getUsername() : "")
                    .nickname(user != null ? user.getNickname() : "")
                    .realName(apply.getRealName())
                    .idCard(apply.getIdCard())
                    .phone(apply.getPhone())
                    .reason(apply.getReason())
                    .status(apply.getStatus())
                    .remark(apply.getRemark())
                    .createTime(apply.getCreateTime())
                    .build();
        }).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public RestResp<Void> auditAuthorApply(Long applyId, Integer status, String remark) {
        AuthorApply apply = authorApplyMapper.selectById(applyId);
        if (apply == null) {
            return RestResp.error("申请记录不存在");
        }
        
        apply.setStatus(status);
        apply.setRemark(remark);
        apply.setUpdateTime(LocalDateTime.now());
        authorApplyMapper.updateById(apply);
        
        // 如果审核通过，更新用户角色为作者(1)
        if (status == 1) {
            userMapper.updateRole(apply.getUserId(), 1);
        }
        
        return RestResp.ok();
    }

    @Override
    public RestResp<List<BookInfoRespDto>> listAllBooks() {
        List<Book> books = bookMapper.selectList(null);
        return RestResp.ok(books.stream().map(this::convertToBookDto).collect(Collectors.toList()));
    }

    @Override
    public RestResp<Void> deleteBook(Long bookId) {
        bookMapper.deleteById(bookId);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> offShelfBook(Long bookId) {
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(2);
        bookMapper.updateById(book);
        return RestResp.ok();
    }

    @Override
    public RestResp<List<CategoryRespDto>> listAllCategories() {
        List<Category> categories = categoryMapper.selectList(null);
        return RestResp.ok(categories.stream().map(this::convertToCategoryDto).collect(Collectors.toList()));
    }

    @Override
    public RestResp<Void> addCategory(CategoryAddReqDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setWorkDirection(dto.getWorkDirection());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> updateCategory(Long categoryId, CategoryAddReqDto dto) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return RestResp.error("分类不存在");
        }
        
        category.setName(dto.getName());
        category.setWorkDirection(dto.getWorkDirection());
        category.setSort(dto.getSort() != null ? dto.getSort() : category.getSort());
        categoryMapper.updateById(category);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> deleteCategory(Long categoryId) {
        // 检查是否有小说使用此分类
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getCategoryId, categoryId);
        if (bookMapper.selectCount(wrapper) > 0) {
            return RestResp.error("该分类下还有小说，无法删除");
        }
        
        categoryMapper.deleteById(categoryId);
        return RestResp.ok();
    }

    @Override
    public RestResp<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("authorCount", userMapper.countByRole(1));
        stats.put("bookCount", bookMapper.selectCount(null));
        stats.put("adminCount", adminMapper.selectCount(null));
        
        // 总收藏数
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        List<Book> books = bookMapper.selectList(bookWrapper);
        long totalFavorite = books.stream().mapToLong(Book::getFavoriteCount).sum();
        stats.put("totalFavoriteCount", totalFavorite);
        
        // 总访问数
        long totalVisit = books.stream().mapToLong(Book::getVisitCount).sum();
        stats.put("totalVisitCount", totalVisit);
        
        return RestResp.ok(stats);
    }

    private UserInfoRespDto convertToUserDto(User user) {
        return UserInfoRespDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .build();
    }

    private BookInfoRespDto convertToBookDto(Book book) {
        return BookInfoRespDto.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .authorName(book.getAuthorName())
                .status(book.getStatus())
                .visitCount(book.getVisitCount())
                .favoriteCount(book.getFavoriteCount())
                .totalWords(book.getTotalWords())
                .updateTime(book.getUpdateTime())
                .build();
    }

    private CategoryRespDto convertToCategoryDto(Category category) {
        return CategoryRespDto.builder()
                .id(category.getId())
                .name(category.getName())
                .workDirection(category.getWorkDirection())
                .sort(category.getSort())
                .build();
    }
}