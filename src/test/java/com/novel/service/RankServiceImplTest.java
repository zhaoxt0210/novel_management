package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.RankBookRespDto;
import com.novel.entity.Book;
import com.novel.entity.BookRank;
import com.novel.mapper.BookFavoriteMapper;
import com.novel.mapper.BookMapper;
import com.novel.mapper.BookRankMapper;
import com.novel.service.impl.RankServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankServiceImplTest {

    @Mock
    private BookRankMapper bookRankMapper;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookFavoriteMapper bookFavoriteMapper;

    @InjectMocks
    private RankServiceImpl rankService;

    @Test
    void getRankListShouldUseFavoriteTableCount() {
        BookRank rank = new BookRank();
        rank.setId(1L);
        rank.setBookId(100L);
        rank.setRankType(2);
        rank.setRankNum(1);

        Book book = new Book();
        book.setId(100L);
        book.setBookName("test-book");
        book.setFavoriteCount(1L);
        book.setVisitCount(99L);

        when(bookRankMapper.selectList(any())).thenReturn(List.of(rank));
        when(bookMapper.selectBatchIds(List.of(100L))).thenReturn(List.of(book));
        when(bookFavoriteMapper.countByBookIds(List.of(100L)))
                .thenReturn(List.of(Map.of("bookId", 100L, "favoriteCount", 12L)));

        RestResp<List<RankBookRespDto>> resp = rankService.getRankList(2, 10);

        assertEquals(200, resp.getCode());
        assertNotNull(resp.getData());
        assertEquals(1, resp.getData().size());
        assertEquals(12L, resp.getData().get(0).getFavoriteCount());
    }

    @Test
    void getRankListShouldFallbackWhenFavoriteAggregationFails() {
        BookRank rank = new BookRank();
        rank.setId(1L);
        rank.setBookId(101L);
        rank.setRankType(2);
        rank.setRankNum(1);

        Book book = new Book();
        book.setId(101L);
        book.setBookName("broken-case");
        book.setVisitCount(5L);
        book.setFavoriteCount(77L);

        when(bookRankMapper.selectList(any())).thenReturn(List.of(rank));
        when(bookMapper.selectBatchIds(List.of(101L))).thenReturn(List.of(book));
        when(bookFavoriteMapper.countByBookIds(List.of(101L))).thenThrow(new RuntimeException("db error"));

        RestResp<List<RankBookRespDto>> resp = rankService.getRankList(2, 10);

        assertEquals(200, resp.getCode());
        assertNotNull(resp.getData());
        assertEquals(1, resp.getData().size());
        assertEquals(0L, resp.getData().get(0).getFavoriteCount());
    }

    @Test
    void getRankListShouldAggregateLargeBatchInSingleQuery() {
        List<BookRank> ranks = IntStream.range(0, 300)
                .mapToObj(i -> {
                    BookRank rank = new BookRank();
                    rank.setId((long) i + 1);
                    rank.setBookId((long) i + 1000);
                    rank.setRankType(2);
                    rank.setRankNum(i + 1);
                    return rank;
                })
                .toList();
        List<Long> bookIds = ranks.stream().map(BookRank::getBookId).toList();
        List<Book> books = bookIds.stream().map(id -> {
            Book b = new Book();
            b.setId(id);
            b.setBookName("book-" + id);
            b.setVisitCount(0L);
            return b;
        }).toList();

        when(bookRankMapper.selectList(any())).thenReturn(ranks);
        when(bookMapper.selectBatchIds(bookIds)).thenReturn(books);
        when(bookFavoriteMapper.countByBookIds(bookIds)).thenReturn(List.of());

        RestResp<List<RankBookRespDto>> resp = rankService.getRankList(2, 300);

        assertEquals(200, resp.getCode());
        assertNotNull(resp.getData());
        assertEquals(300, resp.getData().size());
        verify(bookFavoriteMapper, times(1)).countByBookIds(bookIds);
    }
}
