package com.novel.controller.front;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.BookshelfRespDto;
import com.novel.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "书架模块", description = "书架相关接口")
@RestController
@RequestMapping("/api/bookshelf")
@RequiredArgsConstructor
public class BookshelfController {

    private final BookService bookService;

    @Operation(summary = "获取用户书架")
    @GetMapping("/{userId}")
    public RestResp<List<BookshelfRespDto>> getUserBookshelf(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "create_time") String sortBy) {
        return bookService.getUserBookshelf(userId, sortBy);
    }

    @Operation(summary = "添加到书架")
    @PostMapping("/add")
    public RestResp<Void> addToBookshelf(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.addToBookshelf(userId, bookId);
    }

    @Operation(summary = "从书架移除")
    @DeleteMapping("/remove")
    public RestResp<Void> removeFromBookshelf(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.removeFromBookshelf(userId, bookId);
    }

    @Operation(summary = "检查是否在书架中")
    @GetMapping("/check")
    public RestResp<Boolean> isInBookshelf(@RequestParam Long userId, @RequestParam Long bookId) {
        return bookService.isInBookshelf(userId, bookId);
    }
}