<<<<<<< HEAD
package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookPublishReqDto;
import com.novel.dto.req.ChapterAddReqDto;
import com.novel.dto.req.ChapterUpdateReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookPublishRespDto;
import com.novel.dto.resp.ChapterRespDto;
import com.novel.entity.Book;
import com.novel.entity.Chapter;
import com.novel.entity.User;
import com.novel.mapper.BookMapper;
import com.novel.mapper.ChapterMapper;
import com.novel.mapper.UserMapper;
import com.novel.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional(timeout = 60)
    public RestResp<BookPublishRespDto> saveDraft(Long authorId, BookPublishReqDto dto) {
        return saveBookWithAudit(authorId, dto, 0);
    }

    @Override
    @Transactional(timeout = 60)
    public RestResp<BookPublishRespDto> submitForAudit(Long authorId, BookPublishReqDto dto) {
        if (dto.getChapters() == null || dto.getChapters().isEmpty()) {
            return RestResp.error("请至少添加一个章节");
        }
        return saveBookWithAudit(authorId, dto, 1);
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> submitDraftForAudit(Long authorId, Long bookId) {
        try {
            // 验证用户
            User user = userMapper.selectById(authorId);
            if (user == null || user.getRole() != 1) {
                return RestResp.error("您不是作者");
            }

            // 获取作品
            Book book = bookMapper.selectById(bookId);
            if (book == null) {
                return RestResp.error("作品不存在");
            }
            if (!book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此作品");
            }

            // 只有草稿状态可以提交审核
            if (book.getAuditStatus() == null || book.getAuditStatus() != 0) {
                return RestResp.error("只有草稿状态的作品可以提交审核");
            }

            // 更新作品状态
            Book updateBook = new Book();
            updateBook.setId(bookId);
            updateBook.setAuditStatus(1);
            updateBook.setSubmitTime(LocalDateTime.now());
            updateBook.setUpdateTime(LocalDateTime.now());

            int result = bookMapper.updateById(updateBook);

            if (result > 0) {
                // 更新章节状态
                try {
                    LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Chapter::getBookId, bookId);
                    List<Chapter> chapters = chapterMapper.selectList(wrapper);
                    if (chapters != null && !chapters.isEmpty()) {
                        for (Chapter chapter : chapters) {
                            Chapter updateChapter = new Chapter();
                            updateChapter.setId(chapter.getId());
                            updateChapter.setAuditStatus(1);
                            updateChapter.setUpdateTime(LocalDateTime.now());
                            chapterMapper.updateById(updateChapter);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("更新章节状态失败: " + e.getMessage());
                }
                return RestResp.ok();
            } else {
                return RestResp.error("提交审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("提交审核失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> resubmitForAudit(Long authorId, Long bookId) {
        try {
            User user = userMapper.selectById(authorId);
            if (user == null || user.getRole() != 1) {
                return RestResp.error("您不是作者");
            }

            Book book = bookMapper.selectById(bookId);
            if (book == null) {
                return RestResp.error("作品不存在");
            }
            if (!book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此作品");
            }

            if (book.getAuditStatus() == null || book.getAuditStatus() != 3) {
                return RestResp.error("只有被驳回的作品可以再次提交审核");
            }

            Book updateBook = new Book();
            updateBook.setId(bookId);
            updateBook.setAuditStatus(1);
            updateBook.setSubmitTime(LocalDateTime.now());
            updateBook.setAuditRemark(null);
            updateBook.setUpdateTime(LocalDateTime.now());

            int result = bookMapper.updateById(updateBook);

            if (result > 0) {
                try {
                    LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Chapter::getBookId, bookId);
                    List<Chapter> chapters = chapterMapper.selectList(wrapper);
                    if (chapters != null && !chapters.isEmpty()) {
                        for (Chapter chapter : chapters) {
                            Chapter updateChapter = new Chapter();
                            updateChapter.setId(chapter.getId());
                            updateChapter.setAuditStatus(1);
                            updateChapter.setUpdateTime(LocalDateTime.now());
                            chapterMapper.updateById(updateChapter);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("更新章节状态失败: " + e.getMessage());
                }
                return RestResp.ok();
            } else {
                return RestResp.error("再次提交审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("再次提交审核失败：" + e.getMessage());
        }
    }

    private RestResp<BookPublishRespDto> saveBookWithAudit(Long authorId, BookPublishReqDto dto, Integer auditStatus) {
        try {
            User user = userMapper.selectById(authorId);
            if (user == null) {
                return RestResp.error("用户不存在");
            }
            if (user.getRole() != 1) {
                return RestResp.error("您不是作者，请先申请作者资格");
            }

            if (dto.getBookName() == null || dto.getBookName().trim().isEmpty()) {
                return RestResp.error("书籍标题不能为空");
            }
            if (dto.getBookName().length() < 2 || dto.getBookName().length() > 50) {
                return RestResp.error("书籍标题长度必须在2-50个字符之间");
            }

            if (dto.getCategoryId() == null) {
                return RestResp.error("请选择书籍分类");
            }

            Book book = new Book();
            book.setBookName(dto.getBookName());
            book.setCategoryId(dto.getCategoryId());
            book.setAuthorId(authorId);
            book.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            book.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
            book.setCover(dto.getCover() != null ? dto.getCover() : "");
            book.setStatus(0);
            book.setVisitCount(0L);
            book.setFavoriteCount(0L);
            book.setTotalWords(0);
            book.setAuditStatus(auditStatus);
            book.setCreateTime(LocalDateTime.now());
            book.setUpdateTime(LocalDateTime.now());

            if (auditStatus == 1) {
                book.setSubmitTime(LocalDateTime.now());
            }

            bookMapper.insert(book);
            Long bookId = book.getId();

            int totalWords = 0;
            String lastChapterName = null;
            Long lastChapterId = null;

            for (int i = 0; i < dto.getChapters().size(); i++) {
                BookPublishReqDto.ChapterItem item = dto.getChapters().get(i);

                String safeContent = sanitizeContent(item.getContent());

                Chapter chapter = new Chapter();
                chapter.setBookId(bookId);
                chapter.setChapterNum(i + 1);
                chapter.setChapterName(item.getChapterName());
                chapter.setContent(safeContent);
                chapter.setWordCount(safeContent.length());
                chapter.setStatus(1);
                chapter.setAuditStatus(auditStatus);
                chapter.setCreateTime(LocalDateTime.now());
                chapter.setUpdateTime(LocalDateTime.now());

                chapterMapper.insert(chapter);

                totalWords += chapter.getWordCount();
                lastChapterId = chapter.getId();
                lastChapterName = chapter.getChapterName();
            }

            book.setTotalWords(totalWords);
            book.setLastChapterId(lastChapterId);
            book.setLastChapterName(lastChapterName);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);

            return RestResp.ok(new BookPublishRespDto(bookId, dto.getBookName(), dto.getChapters().size()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("保存失败：" + e.getMessage());
        }
    }

    @Override
    public RestResp<List<BookInfoRespDto>> getMyBooks(Long authorId) {
        try {
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Book::getAuthorId, authorId);
            wrapper.orderByDesc(Book::getUpdateTime);
            List<Book> books = bookMapper.selectList(wrapper);
            return RestResp.ok(books.stream().map(this::convertToBookInfoDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("获取作品列表失败：" + e.getMessage());
        }
    }

    @Override
    public RestResp<List<ChapterRespDto>> getMyChapters(Long authorId, Long bookId) {
        try {
            Book book = bookMapper.selectById(bookId);
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权查看此小说");
            }
            LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Chapter::getBookId, bookId);
            wrapper.orderByAsc(Chapter::getChapterNum);
            List<Chapter> chapters = chapterMapper.selectList(wrapper);
            return RestResp.ok(chapters.stream().map(this::convertToChapterDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("获取章节列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> publishChapter(Long authorId, ChapterAddReqDto dto) {
        try {
            Book book = bookMapper.selectById(dto.getBookId());
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此小说");
            }

            // 修改这里：放宽添加章节的权限
            // 允许添加章节的状态：草稿(0)、已驳回(3)、已发布且未完结(2且status=0)
            boolean canAddChapter = false;

            if (book.getAuditStatus() == null) {
                return RestResp.error("作品状态异常");
            }

            // 草稿状态可以添加
            if (book.getAuditStatus() == 0) {
                canAddChapter = true;
            }
            // 已驳回状态可以添加
            else if (book.getAuditStatus() == 3) {
                canAddChapter = true;
            }
            // 已发布且未完结状态可以添加
            else if (book.getAuditStatus() == 2 && book.getStatus() == 0) {
                canAddChapter = true;
            }

            if (!canAddChapter) {
                return RestResp.error("当前状态无法添加章节。只有草稿、已驳回或已发布未完结的作品可以添加章节。");
            }

            // 获取最大章节号
            LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Chapter::getBookId, dto.getBookId());
            wrapper.orderByDesc(Chapter::getChapterNum);
            wrapper.last("limit 1");
            Chapter lastChapter = chapterMapper.selectOne(wrapper);
            int nextChapterNum = lastChapter == null ? 1 : lastChapter.getChapterNum() + 1;

            // 新章节的审核状态：如果作品是已发布状态，新章节直接设为已发布；否则设为作品相同的审核状态
            int newChapterAuditStatus = book.getAuditStatus();
            if (book.getAuditStatus() == 2) {
                newChapterAuditStatus = 2; // 已发布的作品，新章节直接发布
            }

            Chapter chapter = new Chapter();
            chapter.setBookId(dto.getBookId());
            chapter.setChapterNum(nextChapterNum);
            chapter.setChapterName(dto.getChapterName());
            chapter.setContent(dto.getContent());
            chapter.setWordCount(dto.getContent().length());
            chapter.setStatus(1);
            chapter.setAuditStatus(newChapterAuditStatus);
            chapter.setCreateTime(LocalDateTime.now());
            chapter.setUpdateTime(LocalDateTime.now());
            chapterMapper.insert(chapter);

            // 更新书籍信息
            book.setLastChapterId(chapter.getId());
            book.setLastChapterName(chapter.getChapterName());
            book.setTotalWords(book.getTotalWords() + chapter.getWordCount());
            book.setUpdateTime(LocalDateTime.now());

            // 如果是已驳回状态添加了章节，建议将作品状态重置为草稿，方便重新提交
            if (book.getAuditStatus() == 3) {
                book.setAuditStatus(0); // 改为草稿状态
                book.setAuditRemark(null);
            }

            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("添加章节失败：" + e.getMessage());
        }
    }
    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> updateChapter(Long authorId, ChapterUpdateReqDto dto) {
        try {
            Chapter chapter = chapterMapper.selectById(dto.getChapterId());
            if (chapter == null) {
                return RestResp.error("章节不存在");
            }
            Book book = bookMapper.selectById(chapter.getBookId());
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此章节");
            }

            // 修改这里：放宽修改章节的权限
            boolean canEdit = false;

            if (book.getAuditStatus() == 0) {  // 草稿
                canEdit = true;
            } else if (book.getAuditStatus() == 3) {  // 已驳回
                canEdit = true;
            } else if (book.getAuditStatus() == 2 && book.getStatus() == 0) {  // 已发布且未完结
                canEdit = true;
            }

            if (!canEdit) {
                return RestResp.error("当前状态无法修改章节");
            }

            int oldWordCount = chapter.getWordCount();
            int newWordCount = dto.getContent().length();
            chapter.setContent(dto.getContent());
            chapter.setWordCount(newWordCount);
            chapter.setUpdateTime(LocalDateTime.now());
            chapterMapper.updateById(chapter);

            book.setTotalWords(book.getTotalWords() - oldWordCount + newWordCount);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("更新章节失败：" + e.getMessage());
        }
    }
    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> updateBookInfo(Long authorId, Long bookId, String bookName, String description) {
        try {
            Book book = bookMapper.selectById(bookId);
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作");
            }
            boolean canEdit = (book.getAuditStatus() == 0) || (book.getAuditStatus() == 3);
            if (!canEdit) {
                return RestResp.error("当前状态无法修改作品信息");
            }
            if (bookName != null && !bookName.trim().isEmpty()) {
                book.setBookName(bookName);
            }
            if (description != null) {
                book.setDescription(description);
            }
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("更新作品信息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> updateBookStatus(Long authorId, Long bookId, Integer status) {
        try {
            Book book = bookMapper.selectById(bookId);
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此小说");
            }
            if (book.getAuditStatus() != 2) {
                return RestResp.error("只有已发布的作品可以修改状态");
            }
            book.setStatus(status);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("更新状态失败：" + e.getMessage());
        }
    }

    @Override
    public RestResp<Map<String, Object>> getStatistics(Long authorId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
            bookWrapper.eq(Book::getAuthorId, authorId);
            List<Book> books = bookMapper.selectList(bookWrapper);
            stats.put("bookCount", books.size());
            stats.put("totalVisitCount", books.stream().mapToLong(Book::getVisitCount).sum());
            stats.put("totalFavoriteCount", books.stream().mapToLong(Book::getFavoriteCount).sum());
            stats.put("totalWordCount", books.stream().mapToInt(Book::getTotalWords).sum());
            return RestResp.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("获取统计失败：" + e.getMessage());
        }
    }

    private String sanitizeContent(String content) {
        if (content == null) return "";
        return content.replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<iframe[^>]*>.*?</iframe>", "")
                .replaceAll("javascript:", "");
    }

    private BookInfoRespDto convertToBookInfoDto(Book book) {
        return BookInfoRespDto.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .authorName(book.getAuthorName())
                .categoryId(book.getCategoryId())
                .cover(book.getCover())
                .description(book.getDescription())
                .status(book.getStatus())
                .auditStatus(book.getAuditStatus())
                .visitCount(book.getVisitCount())
                .favoriteCount(book.getFavoriteCount())
                .totalWords(book.getTotalWords())
                .lastChapterName(book.getLastChapterName())
                .lastChapterId(book.getLastChapterId())
                .updateTime(book.getUpdateTime())
                .build();
    }

    private ChapterRespDto convertToChapterDto(Chapter chapter) {
        return ChapterRespDto.builder()
                .id(chapter.getId())
                .chapterNum(chapter.getChapterNum())
                .chapterName(chapter.getChapterName())
                .wordCount(chapter.getWordCount())
                .build();
    }
=======
package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookPublishReqDto;
import com.novel.dto.req.ChapterAddReqDto;
import com.novel.dto.req.ChapterUpdateReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookPublishRespDto;
import com.novel.dto.resp.ChapterRespDto;
import com.novel.entity.Book;
import com.novel.entity.Chapter;
import com.novel.entity.User;
import com.novel.mapper.BookMapper;
import com.novel.mapper.ChapterMapper;
import com.novel.mapper.UserMapper;
import com.novel.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;

    @Override
    @Transactional(timeout = 60)
    public RestResp<BookPublishRespDto> saveDraft(Long authorId, BookPublishReqDto dto) {
        return saveBookWithAudit(authorId, dto, 0);
    }

    @Override
    @Transactional(timeout = 60)
    public RestResp<BookPublishRespDto> submitForAudit(Long authorId, BookPublishReqDto dto) {
        if (dto.getChapters() == null || dto.getChapters().isEmpty()) {
            return RestResp.error("请至少添加一个章节");
        }
        return saveBookWithAudit(authorId, dto, 1);
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> submitDraftForAudit(Long authorId, Long bookId) {
        try {
            // 验证用户
            User user = userMapper.selectById(authorId);
            if (user == null || user.getRole() != 1) {
                return RestResp.error("您不是作者");
            }

            // 获取作品
            Book book = bookMapper.selectById(bookId);
            if (book == null) {
                return RestResp.error("作品不存在");
            }
            if (!book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此作品");
            }

            // 只有草稿状态可以提交审核
            if (book.getAuditStatus() == null || book.getAuditStatus() != 0) {
                return RestResp.error("只有草稿状态的作品可以提交审核");
            }

            // 更新作品状态
            Book updateBook = new Book();
            updateBook.setId(bookId);
            updateBook.setAuditStatus(1);
            updateBook.setSubmitTime(LocalDateTime.now());
            updateBook.setUpdateTime(LocalDateTime.now());

            int result = bookMapper.updateById(updateBook);

            if (result > 0) {
                // 更新章节状态
                try {
                    LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Chapter::getBookId, bookId);
                    List<Chapter> chapters = chapterMapper.selectList(wrapper);
                    if (chapters != null && !chapters.isEmpty()) {
                        for (Chapter chapter : chapters) {
                            Chapter updateChapter = new Chapter();
                            updateChapter.setId(chapter.getId());
                            updateChapter.setAuditStatus(1);
                            updateChapter.setUpdateTime(LocalDateTime.now());
                            chapterMapper.updateById(updateChapter);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("更新章节状态失败: " + e.getMessage());
                }
                return RestResp.ok();
            } else {
                return RestResp.error("提交审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("提交审核失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> resubmitForAudit(Long authorId, Long bookId) {
        try {
            User user = userMapper.selectById(authorId);
            if (user == null || user.getRole() != 1) {
                return RestResp.error("您不是作者");
            }

            Book book = bookMapper.selectById(bookId);
            if (book == null) {
                return RestResp.error("作品不存在");
            }
            if (!book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此作品");
            }

            if (book.getAuditStatus() == null || book.getAuditStatus() != 3) {
                return RestResp.error("只有被驳回的作品可以再次提交审核");
            }

            Book updateBook = new Book();
            updateBook.setId(bookId);
            updateBook.setAuditStatus(1);
            updateBook.setSubmitTime(LocalDateTime.now());
            updateBook.setAuditRemark(null);
            updateBook.setUpdateTime(LocalDateTime.now());

            int result = bookMapper.updateById(updateBook);

            if (result > 0) {
                try {
                    LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Chapter::getBookId, bookId);
                    List<Chapter> chapters = chapterMapper.selectList(wrapper);
                    if (chapters != null && !chapters.isEmpty()) {
                        for (Chapter chapter : chapters) {
                            Chapter updateChapter = new Chapter();
                            updateChapter.setId(chapter.getId());
                            updateChapter.setAuditStatus(1);
                            updateChapter.setUpdateTime(LocalDateTime.now());
                            chapterMapper.updateById(updateChapter);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("更新章节状态失败: " + e.getMessage());
                }
                return RestResp.ok();
            } else {
                return RestResp.error("再次提交审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("再次提交审核失败：" + e.getMessage());
        }
    }

    private RestResp<BookPublishRespDto> saveBookWithAudit(Long authorId, BookPublishReqDto dto, Integer auditStatus) {
        try {
            User user = userMapper.selectById(authorId);
            if (user == null) {
                return RestResp.error("用户不存在");
            }
            if (user.getRole() != 1) {
                return RestResp.error("您不是作者，请先申请作者资格");
            }

            if (dto.getBookName() == null || dto.getBookName().trim().isEmpty()) {
                return RestResp.error("书籍标题不能为空");
            }
            if (dto.getBookName().length() < 2 || dto.getBookName().length() > 50) {
                return RestResp.error("书籍标题长度必须在2-50个字符之间");
            }

            if (dto.getCategoryId() == null) {
                return RestResp.error("请选择书籍分类");
            }

            Book book = new Book();
            book.setBookName(dto.getBookName());
            book.setCategoryId(dto.getCategoryId());
            book.setAuthorId(authorId);
            book.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            book.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
            book.setCover(dto.getCover() != null ? dto.getCover() : "");
            book.setStatus(0);
            book.setVisitCount(0L);
            book.setFavoriteCount(0L);
            book.setTotalWords(0);
            book.setAuditStatus(auditStatus);
            book.setCreateTime(LocalDateTime.now());
            book.setUpdateTime(LocalDateTime.now());

            if (auditStatus == 1) {
                book.setSubmitTime(LocalDateTime.now());
            }

            bookMapper.insert(book);
            Long bookId = book.getId();

            int totalWords = 0;
            String lastChapterName = null;
            Long lastChapterId = null;

            for (int i = 0; i < dto.getChapters().size(); i++) {
                BookPublishReqDto.ChapterItem item = dto.getChapters().get(i);

                String safeContent = sanitizeContent(item.getContent());

                Chapter chapter = new Chapter();
                chapter.setBookId(bookId);
                chapter.setChapterNum(i + 1);
                chapter.setChapterName(item.getChapterName());
                chapter.setContent(safeContent);
                chapter.setWordCount(safeContent.length());
                chapter.setStatus(1);
                chapter.setAuditStatus(auditStatus);
                chapter.setCreateTime(LocalDateTime.now());
                chapter.setUpdateTime(LocalDateTime.now());

                chapterMapper.insert(chapter);

                totalWords += chapter.getWordCount();
                lastChapterId = chapter.getId();
                lastChapterName = chapter.getChapterName();
            }

            book.setTotalWords(totalWords);
            book.setLastChapterId(lastChapterId);
            book.setLastChapterName(lastChapterName);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);

            return RestResp.ok(new BookPublishRespDto(bookId, dto.getBookName(), dto.getChapters().size()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("保存失败：" + e.getMessage());
        }
    }

    @Override
    public RestResp<List<BookInfoRespDto>> getMyBooks(Long authorId) {
        try {
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Book::getAuthorId, authorId);
            wrapper.orderByDesc(Book::getUpdateTime);
            List<Book> books = bookMapper.selectList(wrapper);
            return RestResp.ok(books.stream().map(this::convertToBookInfoDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("获取作品列表失败：" + e.getMessage());
        }
    }

    @Override
    public RestResp<List<ChapterRespDto>> getMyChapters(Long authorId, Long bookId) {
        try {
            Book book = bookMapper.selectById(bookId);
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权查看此小说");
            }
            LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Chapter::getBookId, bookId);
            wrapper.orderByAsc(Chapter::getChapterNum);
            List<Chapter> chapters = chapterMapper.selectList(wrapper);
            return RestResp.ok(chapters.stream().map(this::convertToChapterDto).collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("获取章节列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> publishChapter(Long authorId, ChapterAddReqDto dto) {
        try {
            Book book = bookMapper.selectById(dto.getBookId());
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此小说");
            }

            // 修改这里：放宽添加章节的权限
            // 允许添加章节的状态：草稿(0)、已驳回(3)、已发布且未完结(2且status=0)
            boolean canAddChapter = false;

            if (book.getAuditStatus() == null) {
                return RestResp.error("作品状态异常");
            }

            // 草稿状态可以添加
            if (book.getAuditStatus() == 0) {
                canAddChapter = true;
            }
            // 已驳回状态可以添加
            else if (book.getAuditStatus() == 3) {
                canAddChapter = true;
            }
            // 已发布且未完结状态可以添加
            else if (book.getAuditStatus() == 2 && book.getStatus() == 0) {
                canAddChapter = true;
            }

            if (!canAddChapter) {
                return RestResp.error("当前状态无法添加章节。只有草稿、已驳回或已发布未完结的作品可以添加章节。");
            }

            // 获取最大章节号
            LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Chapter::getBookId, dto.getBookId());
            wrapper.orderByDesc(Chapter::getChapterNum);
            wrapper.last("limit 1");
            Chapter lastChapter = chapterMapper.selectOne(wrapper);
            int nextChapterNum = lastChapter == null ? 1 : lastChapter.getChapterNum() + 1;

            // 新章节的审核状态：如果作品是已发布状态，新章节直接设为已发布；否则设为作品相同的审核状态
            int newChapterAuditStatus = book.getAuditStatus();
            if (book.getAuditStatus() == 2) {
                newChapterAuditStatus = 2; // 已发布的作品，新章节直接发布
            }

            Chapter chapter = new Chapter();
            chapter.setBookId(dto.getBookId());
            chapter.setChapterNum(nextChapterNum);
            chapter.setChapterName(dto.getChapterName());
            chapter.setContent(dto.getContent());
            chapter.setWordCount(dto.getContent().length());
            chapter.setStatus(1);
            chapter.setAuditStatus(newChapterAuditStatus);
            chapter.setCreateTime(LocalDateTime.now());
            chapter.setUpdateTime(LocalDateTime.now());
            chapterMapper.insert(chapter);

            // 更新书籍信息
            book.setLastChapterId(chapter.getId());
            book.setLastChapterName(chapter.getChapterName());
            book.setTotalWords(book.getTotalWords() + chapter.getWordCount());
            book.setUpdateTime(LocalDateTime.now());

            // 如果是已驳回状态添加了章节，建议将作品状态重置为草稿，方便重新提交
            if (book.getAuditStatus() == 3) {
                book.setAuditStatus(0); // 改为草稿状态
                book.setAuditRemark(null);
            }

            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("添加章节失败：" + e.getMessage());
        }
    }
    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> updateChapter(Long authorId, ChapterUpdateReqDto dto) {
        try {
            Chapter chapter = chapterMapper.selectById(dto.getChapterId());
            if (chapter == null) {
                return RestResp.error("章节不存在");
            }
            Book book = bookMapper.selectById(chapter.getBookId());
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此章节");
            }

            // 修改这里：放宽修改章节的权限
            boolean canEdit = false;

            if (book.getAuditStatus() == 0) {  // 草稿
                canEdit = true;
            } else if (book.getAuditStatus() == 3) {  // 已驳回
                canEdit = true;
            } else if (book.getAuditStatus() == 2 && book.getStatus() == 0) {  // 已发布且未完结
                canEdit = true;
            }

            if (!canEdit) {
                return RestResp.error("当前状态无法修改章节");
            }

            int oldWordCount = chapter.getWordCount();
            int newWordCount = dto.getContent().length();
            chapter.setContent(dto.getContent());
            chapter.setWordCount(newWordCount);
            chapter.setUpdateTime(LocalDateTime.now());
            chapterMapper.updateById(chapter);

            book.setTotalWords(book.getTotalWords() - oldWordCount + newWordCount);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("更新章节失败：" + e.getMessage());
        }
    }
    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> updateBookInfo(Long authorId, Long bookId, String bookName, String description) {
        try {
            Book book = bookMapper.selectById(bookId);
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作");
            }
            boolean canEdit = (book.getAuditStatus() == 0) || (book.getAuditStatus() == 3);
            if (!canEdit) {
                return RestResp.error("当前状态无法修改作品信息");
            }
            if (bookName != null && !bookName.trim().isEmpty()) {
                book.setBookName(bookName);
            }
            if (description != null) {
                book.setDescription(description);
            }
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("更新作品信息失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(timeout = 30)
    public RestResp<Void> updateBookStatus(Long authorId, Long bookId, Integer status) {
        try {
            Book book = bookMapper.selectById(bookId);
            if (book == null || !book.getAuthorId().equals(authorId)) {
                return RestResp.error("无权操作此小说");
            }
            if (book.getAuditStatus() != 2) {
                return RestResp.error("只有已发布的作品可以修改状态");
            }
            book.setStatus(status);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
            return RestResp.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("更新状态失败：" + e.getMessage());
        }
    }

    @Override
    public RestResp<Map<String, Object>> getStatistics(Long authorId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
            bookWrapper.eq(Book::getAuthorId, authorId);
            List<Book> books = bookMapper.selectList(bookWrapper);
            stats.put("bookCount", books.size());
            stats.put("totalVisitCount", books.stream().mapToLong(Book::getVisitCount).sum());
            stats.put("totalFavoriteCount", books.stream().mapToLong(Book::getFavoriteCount).sum());
            stats.put("totalWordCount", books.stream().mapToInt(Book::getTotalWords).sum());
            return RestResp.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResp.error("获取统计失败：" + e.getMessage());
        }
    }

    private String sanitizeContent(String content) {
        if (content == null) return "";
        return content.replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<iframe[^>]*>.*?</iframe>", "")
                .replaceAll("javascript:", "");
    }

    private BookInfoRespDto convertToBookInfoDto(Book book) {
        return BookInfoRespDto.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .authorName(book.getAuthorName())
                .categoryId(book.getCategoryId())
                .cover(book.getCover())
                .description(book.getDescription())
                .status(book.getStatus())
                .auditStatus(book.getAuditStatus())
                .visitCount(book.getVisitCount())
                .favoriteCount(book.getFavoriteCount())
                .totalWords(book.getTotalWords())
                .lastChapterName(book.getLastChapterName())
                .lastChapterId(book.getLastChapterId())
                .updateTime(book.getUpdateTime())
                .build();
    }

    private ChapterRespDto convertToChapterDto(Chapter chapter) {
        return ChapterRespDto.builder()
                .id(chapter.getId())
                .chapterNum(chapter.getChapterNum())
                .chapterName(chapter.getChapterName())
                .wordCount(chapter.getWordCount())
                .build();
    }
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}