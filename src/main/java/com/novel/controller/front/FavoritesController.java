package com.novel.controller.front;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收藏模块", description = "收藏相关接口")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final BookService bookService;

    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/{userId}")
    public RestResp<List<BookInfoRespDto>> getUserFavorites(@PathVariable Long userId) {
        return bookService.getUserFavorites(userId);
    }

    @Operation(summary = "添加收藏")
    @PostMapping("/add")
    public RestResp<Void> addFavorite(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.addFavorite(userId, bookId);
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/remove")
    public RestResp<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.removeFavorite(userId, bookId);
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check")
    public RestResp<Boolean> isFavorited(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.isFavorited(userId, bookId);
    }

    @Operation(summary = "添加收藏（自动加入书架）")
    @PostMapping("/add-with-bookshelf")
    public RestResp<Void> addFavoriteWithBookshelf(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.addFavoriteWithBookshelf(userId, bookId);
    }
}