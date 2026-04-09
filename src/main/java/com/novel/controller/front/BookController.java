package com.novel.controller.front;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookSearchReqDto;
import com.novel.dto.req.CommentAddReqDto;
import com.novel.dto.req.ReadProgressReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookshelfRespDto;
import com.novel.dto.resp.ChapterRespDto;
import com.novel.dto.resp.CommentRespDto;
import com.novel.dto.resp.ReadHistoryRespDto;
import com.novel.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "小说模块", description = "小说相关接口")
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "首页推荐小说")
    @GetMapping("/home")
    public RestResp<List<BookInfoRespDto>> listHomeBooks() {
        return bookService.listHomeBooks();
    }

    @Operation(summary = "搜索小说")
    @PostMapping("/search")
    public RestResp<Map<String, Object>> searchBooks(@RequestBody BookSearchReqDto dto) {
        return bookService.searchBooks(dto);
    }

    @Operation(summary = "获取小说详情")
    @GetMapping("/{bookId}")
    public RestResp<BookInfoRespDto> getBookById(@PathVariable Long bookId) {
        return bookService.getBookById(bookId);
    }

    @Operation(summary = "获取小说章节列表")
    @GetMapping("/{bookId}/chapters")
    public RestResp<List<ChapterRespDto>> listChapters(@PathVariable Long bookId) {
        return bookService.listChapters(bookId);
    }

    @Operation(summary = "获取章节内容")
    @GetMapping("/chapter/{chapterId}")
    public RestResp<ChapterRespDto> getChapter(@PathVariable Long chapterId,
                                                @RequestParam(required = false) Long userId) {
        return bookService.getChapter(chapterId, userId);
    }

    @Operation(summary = "增加点击量")
    @PostMapping("/{bookId}/visit")
    public RestResp<Void> addVisitCount(@PathVariable Long bookId) {
        return bookService.addVisitCount(bookId);
    }

    @Operation(summary = "按分类获取小说")
    @GetMapping("/category/{categoryId}")
    public RestResp<List<BookInfoRespDto>> listBooksByCategory(@PathVariable Long categoryId,
                                                                @RequestParam(defaultValue = "update_time") String sortBy,
                                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                                @RequestParam(defaultValue = "20") Integer pageSize) {
        return bookService.listBooksByCategory(categoryId, sortBy, pageNum, pageSize);
    }

    @Operation(summary = "收藏小说")
    @PostMapping("/favorite/add")
    public RestResp<Void> addFavorite(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.addFavorite(userId, bookId);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/remove")
    public RestResp<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.removeFavorite(userId, bookId);
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/favorite/check")
    public RestResp<Boolean> isFavorited(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.isFavorited(userId, bookId);
    }

    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/favorite/{userId}")
    public RestResp<List<BookInfoRespDto>> getUserFavorites(@PathVariable Long userId) {
        return bookService.getUserFavorites(userId);
    }

    @Operation(summary = "获取用户书架")
    @GetMapping("/bookshelf/{userId}")
    public RestResp<List<BookshelfRespDto>> getUserBookshelf(@PathVariable Long userId,
                                                               @RequestParam(defaultValue = "create_time") String sortBy) {
        return bookService.getUserBookshelf(userId, sortBy);
    }

    @Operation(summary = "更新阅读进度")
    @PostMapping("/bookshelf/progress")
    public RestResp<Void> updateReadProgress(@Valid @RequestBody ReadProgressReqDto dto) {
        return bookService.updateReadProgress(dto);
    }

    @Operation(summary = "获取阅读历史")
    @GetMapping("/history/{userId}")
    public RestResp<List<ReadHistoryRespDto>> getReadHistory(@PathVariable Long userId) {
        return bookService.getReadHistory(userId);
    }

    @Operation(summary = "发表评论")
    @PostMapping("/comment/add")
    public RestResp<Void> addComment(@Valid @RequestBody CommentAddReqDto dto) {
        return bookService.addComment(dto);
    }

    @Operation(summary = "获取小说评论列表")
    @GetMapping("/comment/{bookId}")
    public RestResp<List<CommentRespDto>> getComments(@PathVariable Long bookId,
                                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                                       @RequestParam(defaultValue = "20") Integer pageSize) {
        return bookService.getComments(bookId, pageNum, pageSize);
    }

    @Operation(summary = "获取排行榜")
    @GetMapping("/rank/{type}")
    public RestResp<List<BookInfoRespDto>> getRanking(@PathVariable String type,
                                                       @RequestParam(defaultValue = "10") Integer limit) {
        return bookService.getRanking(type, limit);
    }
}