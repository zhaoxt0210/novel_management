package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.CategoryAddReqDto;
import com.novel.dto.resp.AdminLoginRespDto;
import com.novel.dto.resp.AuthorApplyRespDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.CategoryRespDto;
import com.novel.dto.resp.PageRespDto;
import com.novel.dto.resp.UserInfoRespDto;
import com.novel.entity.*;
import com.novel.mapper.*;
import com.novel.security.JwtTokenProvider;
import com.novel.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final CategoryMapper categoryMapper;
    private final AuthorApplyMapper authorApplyMapper;
    private final ChapterMapper chapterMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public RestResp<AdminLoginRespDto> adminLogin(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            return RestResp.error("用户名或密码错误");
        }
        if (admin.getStatus() != 1) {
            return RestResp.error("账号已被禁用");
        }

        String token = jwtTokenProvider.generateTokenFromUsername(username);

        AdminLoginRespDto respDto = AdminLoginRespDto.builder()
                .token(token)
                .adminId(admin.getId())
                .username(admin.getUsername())
                .realName(admin.getRealName())
                .role(admin.getRole())
                .build();

        return RestResp.ok(respDto);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public RestResp<List<AuthorApplyRespDto>> listAuthorApplies(Integer status) {
        long startTime = System.currentTimeMillis();
        
        LambdaQueryWrapper<AuthorApply> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(AuthorApply::getStatus, status);
        }
        wrapper.orderByDesc(AuthorApply::getCreateTime);
        List<AuthorApply> applies = authorApplyMapper.selectList(wrapper);

        log.debug("查询作者申请列表耗时: {}ms, 数量: {}", System.currentTimeMillis() - startTime, applies.size());

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

        if (status == 1) {
            userMapper.updateRole(apply.getUserId(), 1);
        }

        return RestResp.ok();
    }

    @Override
    @Transactional(readOnly = true)
    public RestResp<PageRespDto<BookInfoRespDto>> listAllBooks(Integer pageNum, Integer pageSize, Integer status) {
        long startTime = System.currentTimeMillis();
        
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Book::getAuditStatus, status);
        }
        
        wrapper.orderByDesc(Book::getUpdateTime);

        IPage<Book> bookPage = bookMapper.selectPage(page, wrapper);
        List<Book> books = bookPage.getRecords();

        log.debug("查询书籍列表耗时: {}ms, 数量: {}", System.currentTimeMillis() - startTime, books.size());

        List<BookInfoRespDto> dtoList = convertToBookDtoList(books);

        PageRespDto<BookInfoRespDto> pageResp = PageRespDto.<BookInfoRespDto>builder()
                .list(dtoList)
                .total(bookPage.getTotal())
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) bookPage.getTotal() / pageSize))
                .build();

        return RestResp.ok(pageResp);
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
    @Transactional
    public RestResp<Void> auditBook(Long bookId, Integer auditStatus, String remark) {
        long startTime = System.currentTimeMillis();
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return RestResp.error("作品不存在");
        }

        if (auditStatus < 1 || auditStatus > 3) {
            return RestResp.error("无效的审核状态");
        }

        book.setAuditStatus(auditStatus);
        book.setAuditRemark(remark);
        book.setAuditTime(LocalDateTime.now());

        if (auditStatus == 2) {
            if (book.getStatus() == null) {
                book.setStatus(0);
            }
            chapterMapper.batchUpdateAuditStatus(bookId, 2, LocalDateTime.now());
        }

        bookMapper.updateById(book);

        log.debug("审核作品耗时: {}ms, bookId: {}, auditStatus: {}", 
                System.currentTimeMillis() - startTime, bookId, auditStatus);

        return RestResp.ok();
    }

    @Override
    @Transactional(readOnly = true)
    public RestResp<PageRespDto<BookInfoRespDto>> getPendingBooks(Integer pageNum, Integer pageSize) {
        long startTime = System.currentTimeMillis();
        
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getAuditStatus, 1);
        wrapper.orderByDesc(Book::getSubmitTime);

        IPage<Book> bookPage = bookMapper.selectPage(page, wrapper);
        List<Book> books = bookPage.getRecords();

        log.debug("查询待审核作品耗时: {}ms, 数量: {}", System.currentTimeMillis() - startTime, books.size());

        List<BookInfoRespDto> dtoList = convertToBookDtoList(books);

        PageRespDto<BookInfoRespDto> pageResp = PageRespDto.<BookInfoRespDto>builder()
                .list(dtoList)
                .total(bookPage.getTotal())
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) bookPage.getTotal() / pageSize))
                .build();

        return RestResp.ok(pageResp);
    }

    @Override
    @Transactional(readOnly = true)
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
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getCategoryId, categoryId);
        if (bookMapper.selectCount(wrapper) > 0) {
            return RestResp.error("该分类下还有小说，无法删除");
        }

        categoryMapper.deleteById(categoryId);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> resetPassword(Long adminId, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            return RestResp.error("管理员不存在");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(encodedPassword);
        adminMapper.updateById(admin);
        return RestResp.ok();
    }

    private List<BookInfoRespDto> convertToBookDtoList(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return List.of();
        }

        List<Long> categoryIds = books.stream()
                .map(Book::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> categoryNameMap = categoryIds.isEmpty() 
                ? Map.of() 
                : categoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(Collectors.toMap(Category::getId, Category::getName));

        return books.stream().map(book -> BookInfoRespDto.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .authorName(book.getAuthorName())
                .categoryName(categoryNameMap.getOrDefault(book.getCategoryId(), ""))
                .status(book.getStatus())
                .auditStatus(book.getAuditStatus())
                .visitCount(book.getVisitCount())
                .favoriteCount(book.getFavoriteCount())
                .totalWords(book.getTotalWords())
                .updateTime(book.getUpdateTime())
                .build()).collect(Collectors.toList());
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

    private CategoryRespDto convertToCategoryDto(Category category) {
        return CategoryRespDto.builder()
                .id(category.getId())
                .name(category.getName())
                .workDirection(category.getWorkDirection())
                .sort(category.getSort())
                .build();
    }
}