package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookPublishReqDto;
import com.novel.dto.req.ChapterAddReqDto;
import com.novel.dto.req.ChapterUpdateReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookPublishRespDto;
import com.novel.dto.resp.BookSimpleInfoRespDto;
import com.novel.dto.resp.ChapterRespDto;
import java.util.List;
import java.util.Map;

public interface AuthorService {
    RestResp<BookPublishRespDto> saveDraft(Long authorId, BookPublishReqDto dto);
    RestResp<BookPublishRespDto> submitForAudit(Long authorId, BookPublishReqDto dto);
    RestResp<Void> submitDraftForAudit(Long authorId, Long bookId);
    RestResp<Void> resubmitForAudit(Long authorId, Long bookId);
    RestResp<List<BookSimpleInfoRespDto>> getMyBooks(Long authorId);
    RestResp<List<ChapterRespDto>> getMyChapters(Long authorId, Long bookId);
    RestResp<Void> publishChapter(Long authorId, ChapterAddReqDto dto);
    RestResp<Void> updateChapter(Long authorId, ChapterUpdateReqDto dto);
    RestResp<Void> updateBookInfo(Long authorId, Long bookId, String bookName, String description);
    RestResp<Void> updateBookStatus(Long authorId, Long bookId, Integer status);
    RestResp<Map<String, Object>> getStatistics(Long authorId);
}