package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookPublishReqDto;
import com.novel.dto.req.ChapterAddReqDto;
import com.novel.dto.req.ChapterUpdateReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookPublishRespDto;
import com.novel.dto.resp.ChapterRespDto;
import java.util.List;
import java.util.Map;

public interface AuthorService {
    // 保存草稿
    RestResp<BookPublishRespDto> saveDraft(Long authorId, BookPublishReqDto dto);

    // 提交审核（新作品）
    RestResp<BookPublishRespDto> submitForAudit(Long authorId, BookPublishReqDto dto);

    // 草稿作品提交审核
    RestResp<Void> submitDraftForAudit(Long authorId, Long bookId);

    // 被驳回作品再次提交审核
    RestResp<Void> resubmitForAudit(Long authorId, Long bookId);

    // 获取我的作品列表
    RestResp<List<BookInfoRespDto>> getMyBooks(Long authorId);

    // 获取章节列表
    RestResp<List<ChapterRespDto>> getMyChapters(Long authorId, Long bookId);

    // 发布章节
    RestResp<Void> publishChapter(Long authorId, ChapterAddReqDto dto);

    // 更新章节
    RestResp<Void> updateChapter(Long authorId, ChapterUpdateReqDto dto);

    // 更新书籍信息
    RestResp<Void> updateBookInfo(Long authorId, Long bookId, String bookName, String description);

    // 更新小说状态
    RestResp<Void> updateBookStatus(Long authorId, Long bookId, Integer status);

    // 统计信息
    RestResp<Map<String, Object>> getStatistics(Long authorId);
}