package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.req.BookSearchReqDto;
import com.novel.dto.req.CommentAddReqDto;
import com.novel.dto.req.ReadProgressReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.dto.resp.BookshelfRespDto;
import com.novel.dto.resp.ChapterRespDto;
import com.novel.dto.resp.CommentRespDto;
import com.novel.dto.resp.ReadHistoryRespDto;
import java.util.List;
import java.util.Map;

public interface BookService {
    RestResp<List<BookInfoRespDto>> listHomeBooks();
    RestResp<Map<String, Object>> searchBooks(BookSearchReqDto dto);
    RestResp<BookInfoRespDto> getBookById(Long bookId);
    RestResp<List<ChapterRespDto>> listChapters(Long bookId);
    RestResp<ChapterRespDto> getChapter(Long chapterId, Long userId);
    RestResp<Void> addVisitCount(Long bookId);
    RestResp<List<BookInfoRespDto>> listBooksByCategory(Long categoryId, String sortBy, Integer pageNum, Integer pageSize);
    
    // 收藏功能
    RestResp<Void> addFavorite(Long userId, Long bookId);
    RestResp<Void> removeFavorite(Long userId, Long bookId);
    RestResp<Boolean> isFavorited(Long userId, Long bookId);
    RestResp<List<BookInfoRespDto>> getUserFavorites(Long userId);
    
    // 书架功能
    RestResp<List<BookshelfRespDto>> getUserBookshelf(Long userId, String sortBy);
    RestResp<Void> updateReadProgress(ReadProgressReqDto dto);
    
    // 阅读历史
    RestResp<List<ReadHistoryRespDto>> getReadHistory(Long userId);
    
    // 评论功能
    RestResp<Void> addComment(CommentAddReqDto dto);
    RestResp<List<CommentRespDto>> getComments(Long bookId, Integer pageNum, Integer pageSize);
    
    // 排行榜
    RestResp<List<BookInfoRespDto>> getRanking(String type, Integer limit);
    
    // 作家功能 - 获取作者的所有小说
    RestResp<List<BookInfoRespDto>> getAuthorBooks(Long authorId);
}