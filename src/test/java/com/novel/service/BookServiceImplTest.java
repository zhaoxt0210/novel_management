package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.entity.Book;
import com.novel.mapper.BookFavoriteMapper;
import com.novel.mapper.BookMapper;
import com.novel.mapper.CategoryMapper;
import com.novel.mapper.ChapterMapper;
import com.novel.mapper.CommentMapper;
import com.novel.mapper.ReadHistoryMapper;
import com.novel.mapper.UserMapper;
import com.novel.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookMapper bookMapper;
    @Mock
    private ChapterMapper chapterMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BookFavoriteMapper bookFavoriteMapper;
    @Mock
    private ReadHistoryMapper readHistoryMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void addFavoriteShouldSyncCountFromFavoriteTable() {
        Book book = new Book();
        book.setId(10L);
        when(bookMapper.selectById(10L)).thenReturn(book);
        when(bookFavoriteMapper.selectCount(any())).thenReturn(0L);

        RestResp<Void> resp = bookService.addFavorite(1L, 10L);

        assertEquals(200, resp.getCode());
        verify(bookMapper).syncFavoriteCount(10L);
    }

    @Test
    void removeFavoriteShouldReturnErrorWhenNotFavorited() {
        when(bookFavoriteMapper.delete(any())).thenReturn(0);

        RestResp<Void> resp = bookService.removeFavorite(1L, 10L);

        assertEquals(500, resp.getCode());
        assertEquals("未收藏该书籍", resp.getMsg());
        verify(bookMapper, never()).syncFavoriteCount(10L);
    }

    @Test
    void removeFavoriteShouldSyncCountAfterDelete() {
        when(bookFavoriteMapper.delete(any())).thenReturn(1);

        RestResp<Void> resp = bookService.removeFavorite(1L, 10L);

        assertEquals(200, resp.getCode());
        verify(bookMapper).syncFavoriteCount(10L);
    }
}
