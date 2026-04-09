package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookAddReqDto;
import com.novel.dto.req.ChapterAddReqDto;
import com.novel.dto.req.ChapterUpdateReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.ChapterRespDto;
import java.util.List;
import java.util.Map;

public interface AuthorService {
    RestResp<Void> publishBook(Long authorId, BookAddReqDto dto);
    RestResp<Void> publishChapter(Long authorId, ChapterAddReqDto dto);
    RestResp<Void> updateChapter(Long authorId, ChapterUpdateReqDto dto);
    RestResp<List<BookInfoRespDto>> getMyBooks(Long authorId);
    RestResp<List<ChapterRespDto>> getMyChapters(Long authorId, Long bookId);
    RestResp<Map<String, Object>> getStatistics(Long authorId);
    RestResp<Void> updateBookStatus(Long authorId, Long bookId, Integer status);
}