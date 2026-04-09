package com.novel.controller.author;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookAddReqDto;
import com.novel.dto.req.ChapterAddReqDto;
import com.novel.dto.req.ChapterUpdateReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.ChapterRespDto;
import com.novel.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "作家模块", description = "作家后台接口")
@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "发布小说")
    @PostMapping("/{authorId}/book")
    public RestResp<Void> publishBook(@PathVariable Long authorId, @Valid @RequestBody BookAddReqDto dto) {
        return authorService.publishBook(authorId, dto);
    }

    @Operation(summary = "发布章节")
    @PostMapping("/{authorId}/chapter")
    public RestResp<Void> publishChapter(@PathVariable Long authorId, @Valid @RequestBody ChapterAddReqDto dto) {
        return authorService.publishChapter(authorId, dto);
    }

    @Operation(summary = "更新章节")
    @PutMapping("/{authorId}/chapter")
    public RestResp<Void> updateChapter(@PathVariable Long authorId, @Valid @RequestBody ChapterUpdateReqDto dto) {
        return authorService.updateChapter(authorId, dto);
    }

    @Operation(summary = "获取我的小说列表")
    @GetMapping("/{authorId}/books")
    public RestResp<List<BookInfoRespDto>> getMyBooks(@PathVariable Long authorId) {
        return authorService.getMyBooks(authorId);
    }

    @Operation(summary = "获取小说章节列表")
    @GetMapping("/{authorId}/books/{bookId}/chapters")
    public RestResp<List<ChapterRespDto>> getMyChapters(@PathVariable Long authorId, @PathVariable Long bookId) {
        return authorService.getMyChapters(authorId, bookId);
    }

    @Operation(summary = "获取小说统计")
    @GetMapping("/{authorId}/statistics")
    public RestResp<Map<String, Object>> getStatistics(@PathVariable Long authorId) {
        return authorService.getStatistics(authorId);
    }

    @Operation(summary = "更新小说状态")
    @PutMapping("/{authorId}/book/{bookId}/status")
    public RestResp<Void> updateBookStatus(@PathVariable Long authorId, 
                                            @PathVariable Long bookId,
                                            @RequestParam Integer status) {
        return authorService.updateBookStatus(authorId, bookId, status);
    }
}