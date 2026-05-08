package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookAddReqDto;
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
import com.novel.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;
    private final BookService bookService;

    @Override
    @Transactional
    public RestResp<Void> publishBook(Long authorId, BookAddReqDto dto) {
        // 验证作者身份
        User user = userMapper.selectById(authorId);
        if (user == null || user.getRole() != 1) {
            return RestResp.error("您不是作者，请先申请作者资格");
        }
        
        Book book = new Book();
        book.setBookName(dto.getBookName());
        book.setCategoryId(dto.getCategoryId());
        book.setAuthorId(authorId);
        book.setAuthorName(user.getNickname());
        book.setDescription(dto.getDescription());
        book.setCover(dto.getCover());
        book.setStatus(0); // 连载中
        book.setVisitCount(0L);
        book.setFavoriteCount(0L);
        book.setTotalWords(0);
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        
        bookMapper.insert(book);
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<BookPublishRespDto> publishBookWithChapters(Long authorId, BookPublishReqDto dto) {
        User user = userMapper.selectById(authorId);
        if (user == null) {
            return RestResp.error("用户不存在");
        }
        if (user.getRole() != 1) {
            return RestResp.error("您不是作者，请先申请作者资格");
        }

        LambdaQueryWrapper<Book> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(Book::getBookName, dto.getBookName());
        nameWrapper.eq(Book::getAuthorId, authorId);
        if (bookMapper.exists(nameWrapper)) {
            return RestResp.error("您已发布过同名作品");
        }

        Book book = new Book();
        book.setBookName(dto.getBookName());
        book.setCategoryId(dto.getCategoryId());
        book.setAuthorId(authorId);
        book.setAuthorName(user.getNickname());
        book.setDescription(dto.getDescription());
        book.setCover(dto.getCover());
        book.setStatus(dto.getPublishStatus());
        book.setVisitCount(0L);
        book.setFavoriteCount(0L);
        book.setTotalWords(0);
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());

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
            chapter.setStatus(dto.getPublishStatus());
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
    }

    private String sanitizeContent(String content) {
        if (content == null) {
            return "";
        }
        return content.replaceAll("<script[^>]*>.*?</script>", "")
                      .replaceAll("<iframe[^>]*>.*?</iframe>", "")
                      .replaceAll("javascript:", "");
    }

    @Override
    @Transactional
    public RestResp<Void> publishChapter(Long authorId, ChapterAddReqDto dto) {
        // 验证小说作者
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null || !book.getAuthorId().equals(authorId)) {
            return RestResp.error("无权操作此小说");
        }
        
        // 获取最大章节号
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getBookId, dto.getBookId());
        wrapper.orderByDesc(Chapter::getChapterNum);
        wrapper.last("limit 1");
        Chapter lastChapter = chapterMapper.selectOne(wrapper);
        int nextChapterNum = lastChapter == null ? 1 : lastChapter.getChapterNum() + 1;
        
        Chapter chapter = new Chapter();
        chapter.setBookId(dto.getBookId());
        chapter.setChapterNum(nextChapterNum);
        chapter.setChapterName(dto.getChapterName());
        chapter.setContent(dto.getContent());
        chapter.setWordCount(dto.getContent().length());
        chapter.setStatus(1); // 已发布
        chapter.setCreateTime(LocalDateTime.now());
        chapter.setUpdateTime(LocalDateTime.now());
        
        chapterMapper.insert(chapter);
        
        // 更新小说的最新章节和总字数
        book.setLastChapterId(chapter.getId());
        book.setLastChapterName(chapter.getChapterName());
        book.setTotalWords(book.getTotalWords() + chapter.getWordCount());
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);
        
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> updateChapter(Long authorId, ChapterUpdateReqDto dto) {
        Chapter chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null) {
            return RestResp.error("章节不存在");
        }
        
        Book book = bookMapper.selectById(chapter.getBookId());
        if (book == null || !book.getAuthorId().equals(authorId)) {
            return RestResp.error("无权操作此章节");
        }
        
        int oldWordCount = chapter.getWordCount();
        int newWordCount = dto.getContent().length();
        
        chapter.setContent(dto.getContent());
        chapter.setWordCount(newWordCount);
        chapter.setUpdateTime(LocalDateTime.now());
        chapterMapper.updateById(chapter);
        
        // 更新小说总字数
        book.setTotalWords(book.getTotalWords() - oldWordCount + newWordCount);
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);
        
        return RestResp.ok();
    }

    @Override
    public RestResp<List<BookInfoRespDto>> getMyBooks(Long authorId) {
        // 调用 BookService 的 getAuthorBooks 方法获取作者的所有小说
        return bookService.getAuthorBooks(authorId);
    }

    @Override
    public RestResp<List<ChapterRespDto>> getMyChapters(Long authorId, Long bookId) {
        // 验证权限
        Book book = bookMapper.selectById(bookId);
        if (book == null || !book.getAuthorId().equals(authorId)) {
            return RestResp.error("无权查看此小说");
        }
        
        // 调用 BookService 的 listChapters 方法获取章节列表
        return bookService.listChapters(bookId);
    }

    @Override
    public RestResp<Map<String, Object>> getStatistics(Long authorId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取作者所有小说
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        bookWrapper.eq(Book::getAuthorId, authorId);
        List<Book> books = bookMapper.selectList(bookWrapper);
        
        stats.put("bookCount", books.size());
        stats.put("totalVisitCount", books.stream().mapToLong(Book::getVisitCount).sum());
        stats.put("totalFavoriteCount", books.stream().mapToLong(Book::getFavoriteCount).sum());
        stats.put("totalWordCount", books.stream().mapToInt(Book::getTotalWords).sum());
        
        // 获取连载中和已完结的小说数量
        long ongoingCount = books.stream().filter(book -> book.getStatus() == 0).count();
        long finishedCount = books.stream().filter(book -> book.getStatus() == 1).count();
        stats.put("ongoingCount", ongoingCount);
        stats.put("finishedCount", finishedCount);
        
        return RestResp.ok(stats);
    }

    @Override
    @Transactional
    public RestResp<Void> updateBookStatus(Long authorId, Long bookId, Integer status) {
        Book book = bookMapper.selectById(bookId);
        if (book == null || !book.getAuthorId().equals(authorId)) {
            return RestResp.error("无权操作此小说");
        }
        
        if (status < 0 || status > 2) {
            return RestResp.error("状态值无效");
        }
        
        book.setStatus(status);
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);
        
        return RestResp.ok();
    }
}