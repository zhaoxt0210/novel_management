package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookSearchReqDto;
import com.novel.dto.req.CommentAddReqDto;
import com.novel.dto.req.ReadProgressReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookshelfRespDto;
import com.novel.dto.resp.ChapterRespDto;
import com.novel.dto.resp.CommentRespDto;
import com.novel.dto.resp.ReadHistoryRespDto;
import com.novel.entity.*;
import com.novel.mapper.*;
import com.novel.service.BookService;
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
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final ChapterMapper chapterMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final BookFavoriteMapper bookFavoriteMapper;
    private final ReadHistoryMapper readHistoryMapper;
    private final CommentMapper commentMapper;

    @Override
    public RestResp<List<BookInfoRespDto>> listHomeBooks() {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 0);
        wrapper.orderByDesc(Book::getUpdateTime);
        wrapper.last("limit 10");
        List<Book> books = bookMapper.selectList(wrapper);

        return RestResp.ok(books.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @Override
    public RestResp<Map<String, Object>> searchBooks(BookSearchReqDto dto) {
        Map<String, Object> result = new HashMap<>();
        
        // 构建查询条件
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 0);
        
        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            wrapper.and(w -> w.like(Book::getBookName, dto.getKeyword())
                    .or().like(Book::getAuthorName, dto.getKeyword()));
        }
        
        if (dto.getCategoryId() != null) {
            wrapper.eq(Book::getCategoryId, dto.getCategoryId());
        }
        
        // 排序
        if ("visit_count".equals(dto.getSortBy())) {
            wrapper.orderByDesc(Book::getVisitCount);
        } else if ("favorite_count".equals(dto.getSortBy())) {
            wrapper.orderByDesc(Book::getFavoriteCount);
        } else {
            wrapper.orderByDesc(Book::getUpdateTime);
        }
        
        // 分页
        Page<Book> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Book> bookPage = bookMapper.selectPage(page, wrapper);
        
        List<BookInfoRespDto> list = bookPage.getRecords().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        result.put("list", list);
        result.put("total", bookPage.getTotal());
        result.put("pageNum", dto.getPageNum());
        result.put("pageSize", dto.getPageSize());
        
        return RestResp.ok(result);
    }

    @Override
    public RestResp<BookInfoRespDto> getBookById(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return RestResp.error("小说不存在");
        }
        return RestResp.ok(convertToDto(book));
    }

    @Override
    public RestResp<List<ChapterRespDto>> listChapters(Long bookId) {
        LambdaQueryWrapper<Chapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Chapter::getBookId, bookId);
        wrapper.eq(Chapter::getStatus, 1);
        wrapper.orderByAsc(Chapter::getChapterNum);
        List<Chapter> chapters = chapterMapper.selectList(wrapper);

        return RestResp.ok(chapters.stream().map(this::convertToChapterDto).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public RestResp<ChapterRespDto> getChapter(Long chapterId, Long userId) {
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            return RestResp.error("章节不存在");
        }
        
        Book book = bookMapper.selectById(chapter.getBookId());
        if (book == null) {
            return RestResp.error("小说不存在");
        }
        
        // 获取上下章节
        Long prevChapterId = chapterMapper.getPrevChapterId(book.getId(), chapter.getChapterNum());
        Long nextChapterId = chapterMapper.getNextChapterId(book.getId(), chapter.getChapterNum());
        
        ChapterRespDto dto = ChapterRespDto.builder()
                .id(chapter.getId())
                .chapterNum(chapter.getChapterNum())
                .chapterName(chapter.getChapterName())
                .content(chapter.getContent())
                .wordCount(chapter.getWordCount())
                .prevChapterId(prevChapterId)
                .nextChapterId(nextChapterId)
                .build();
        
        // 记录阅读历史
        if (userId != null) {
            ReadHistory history = new ReadHistory();
            history.setUserId(userId);
            history.setBookId(book.getId());
            history.setChapterId(chapter.getId());
            history.setChapterNum(chapter.getChapterNum());
            history.setChapterName(chapter.getChapterName());
            history.setCreateTime(LocalDateTime.now());
            readHistoryMapper.insert(history);
        }
        
        return RestResp.ok(dto);
    }

    @Override
    @Transactional
    public RestResp<Void> addVisitCount(Long bookId) {
        bookMapper.addVisitCount(bookId);
        return RestResp.ok();
    }

    @Override
    public RestResp<List<BookInfoRespDto>> listBooksByCategory(Long categoryId, String sortBy, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        String sortColumn = "update_time";
        
        if ("visit_count".equals(sortBy)) {
            sortColumn = "visit_count";
        } else if ("favorite_count".equals(sortBy)) {
            sortColumn = "favorite_count";
        }
        
        List<Book> books = bookMapper.getBooksByCategory(categoryId, sortColumn, offset, pageSize);
        return RestResp.ok(books.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public RestResp<Void> addFavorite(Long userId, Long bookId) {
        // 检查书籍是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            return RestResp.error("书籍不存在");
        }
        
        // 检查是否已收藏
        LambdaQueryWrapper<BookFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookFavorite::getUserId, userId)
                .eq(BookFavorite::getBookId, bookId);
        if (bookFavoriteMapper.selectCount(wrapper) > 0) {
            return RestResp.error("已经收藏过了");
        }
        
        // 添加收藏
        BookFavorite favorite = new BookFavorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setCreateTime(LocalDateTime.now());
        bookFavoriteMapper.insert(favorite);
        
        // 更新小说收藏数
        bookMapper.incrementFavoriteCount(bookId);
        
        return RestResp.ok();
    }

    @Override
    @Transactional
    public RestResp<Void> removeFavorite(Long userId, Long bookId) {
        LambdaQueryWrapper<BookFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookFavorite::getUserId, userId)
                .eq(BookFavorite::getBookId, bookId);
        bookFavoriteMapper.delete(wrapper);
        
        // 更新小说收藏数
        bookMapper.decrementFavoriteCount(bookId);
        
        return RestResp.ok();
    }

    @Override
    public RestResp<Boolean> isFavorited(Long userId, Long bookId) {
        LambdaQueryWrapper<BookFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookFavorite::getUserId, userId)
                .eq(BookFavorite::getBookId, bookId);
        boolean exists = bookFavoriteMapper.selectCount(wrapper) > 0;
        return RestResp.ok(exists);
    }

    @Override
    public RestResp<List<BookInfoRespDto>> getUserFavorites(Long userId) {
        LambdaQueryWrapper<BookFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookFavorite::getUserId, userId)
                .orderByDesc(BookFavorite::getCreateTime);
        List<BookFavorite> favorites = bookFavoriteMapper.selectList(wrapper);
        
        if (favorites.isEmpty()) {
            return RestResp.ok(List.of());
        }
        
        List<Long> bookIds = favorites.stream()
                .map(BookFavorite::getBookId)
                .collect(Collectors.toList());
        
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        return RestResp.ok(books.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @Override
    public RestResp<List<BookshelfRespDto>> getUserBookshelf(Long userId, String sortBy) {
        List<BookFavorite> favorites;
        
        if ("last_read".equals(sortBy)) {
            favorites = bookFavoriteMapper.selectByUserIdOrderByLastRead(userId);
        } else if ("progress".equals(sortBy)) {
            favorites = bookFavoriteMapper.selectByUserIdOrderByProgress(userId);
        } else {
            favorites = bookFavoriteMapper.selectByUserId(userId);
        }
        
        if (favorites.isEmpty()) {
            return RestResp.ok(List.of());
        }
        
        List<Long> bookIds = favorites.stream()
                .map(BookFavorite::getBookId)
                .collect(Collectors.toList());
        
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));
        
        return RestResp.ok(favorites.stream().map(fav -> {
            Book book = bookMap.get(fav.getBookId());
            if (book == null) return null;
            
            return BookshelfRespDto.builder()
                    .id(fav.getId())
                    .bookId(book.getId())
                    .bookName(book.getBookName())
                    .authorName(book.getAuthorName())
                    .cover(book.getCover())
                    .description(book.getDescription())
                    .totalWords(book.getTotalWords())
                    .favoriteCount(book.getFavoriteCount())
                    .status(book.getStatus())
                    .lastReadChapterId(fav.getLastReadChapterId())
                    .lastReadChapterNum(fav.getLastReadChapterNum())
                    .lastReadChapterName(fav.getLastReadChapterName())
                    .readProgress(fav.getReadProgress())
                    .addTime(fav.getCreateTime())
                    .lastReadTime(fav.getLastReadTime())
                    .build();
        }).filter(item -> item != null).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public RestResp<Void> updateReadProgress(ReadProgressReqDto dto) {
        BookFavorite favorite = bookFavoriteMapper.selectByUserIdAndBookId(dto.getUserId(), dto.getBookId());
        if (favorite == null) {
            return RestResp.error("该书籍不在书架中");
        }
        
        bookFavoriteMapper.updateReadProgress(
                dto.getUserId(),
                dto.getBookId(),
                dto.getChapterId(),
                dto.getChapterNum(),
                dto.getChapterName(),
                dto.getReadProgress()
        );
        
        return RestResp.ok();
    }

    @Override
    public RestResp<List<ReadHistoryRespDto>> getReadHistory(Long userId) {
        LambdaQueryWrapper<ReadHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReadHistory::getUserId, userId)
                .orderByDesc(ReadHistory::getCreateTime)
                .last("limit 50");
        List<ReadHistory> histories = readHistoryMapper.selectList(wrapper);
        
        if (histories.isEmpty()) {
            return RestResp.ok(List.of());
        }
        
        // 去重，每个小说只保留最新的一条
        Map<Long, ReadHistory> uniqueHistories = new HashMap<>();
        for (ReadHistory history : histories) {
            if (!uniqueHistories.containsKey(history.getBookId())) {
                uniqueHistories.put(history.getBookId(), history);
            }
        }
        
        List<ReadHistory> uniqueList = uniqueHistories.values().stream()
                .collect(Collectors.toList());
        
        List<Long> bookIds = uniqueList.stream()
                .map(ReadHistory::getBookId)
                .collect(Collectors.toList());
        
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        Map<Long, Book> bookMap = books.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));
        
        return RestResp.ok(uniqueList.stream().map(history -> {
            Book book = bookMap.get(history.getBookId());
            return ReadHistoryRespDto.builder()
                    .id(history.getId())
                    .bookId(history.getBookId())
                    .bookName(book != null ? book.getBookName() : "")
                    .authorName(book != null ? book.getAuthorName() : "")
                    .cover(book != null ? book.getCover() : "")
                    .chapterId(history.getChapterId())
                    .chapterNum(history.getChapterNum())
                    .chapterName(history.getChapterName())
                    .readTime(history.getCreateTime())
                    .build();
        }).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public RestResp<Void> addComment(CommentAddReqDto dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            return RestResp.error("用户不存在");
        }
        
        Book book = bookMapper.selectById(dto.getBookId());
        if (book == null) {
            return RestResp.error("小说不存在");
        }
        
        Comment comment = new Comment();
        comment.setUserId(dto.getUserId());
        comment.setBookId(dto.getBookId());
        comment.setContent(dto.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        
        commentMapper.insert(comment);
        return RestResp.ok();
    }

    @Override
    public RestResp<List<CommentRespDto>> getComments(Long bookId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getBookId, bookId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        IPage<Comment> commentPage = commentMapper.selectPage(page, wrapper);
        
        if (commentPage.getRecords().isEmpty()) {
            return RestResp.ok(List.of());
        }
        
        List<Long> userIds = commentPage.getRecords().stream()
                .map(Comment::getUserId)
                .collect(Collectors.toList());
        
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        
        return RestResp.ok(commentPage.getRecords().stream().map(comment -> {
            User user = userMap.get(comment.getUserId());
            return CommentRespDto.builder()
                    .id(comment.getId())
                    .userId(comment.getUserId())
                    .username(user != null ? user.getUsername() : "")
                    .nickname(user != null ? user.getNickname() : "")
                    .avatar(user != null ? user.getAvatar() : "")
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .createTime(comment.getCreateTime())
                    .build();
        }).collect(Collectors.toList()));
    }

    @Override
    public RestResp<List<BookInfoRespDto>> getRanking(String type, Integer limit) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 0);
        
        if ("visit".equals(type)) {
            wrapper.orderByDesc(Book::getVisitCount);
        } else if ("favorite".equals(type)) {
            wrapper.orderByDesc(Book::getFavoriteCount);
        } else {
            wrapper.orderByDesc(Book::getUpdateTime);
        }
        
        wrapper.last("limit " + limit);
        List<Book> books = bookMapper.selectList(wrapper);
        
        return RestResp.ok(books.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    @Override
    public RestResp<List<BookInfoRespDto>> getAuthorBooks(Long authorId) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getAuthorId, authorId);
        wrapper.orderByDesc(Book::getUpdateTime);
        List<Book> books = bookMapper.selectList(wrapper);
        
        return RestResp.ok(books.stream().map(this::convertToDto).collect(Collectors.toList()));
    }

    private BookInfoRespDto convertToDto(Book book) {
        Category category = categoryMapper.selectById(book.getCategoryId());
        
        return BookInfoRespDto.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .categoryName(category != null ? category.getName() : "")
                .categoryId(book.getCategoryId())
                .authorId(book.getAuthorId())
                .authorName(book.getAuthorName())
                .cover(book.getCover())
                .description(book.getDescription())
                .status(book.getStatus())
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
}