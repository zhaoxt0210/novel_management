package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.entity.Book;
import com.novel.mapper.BookMapper;
import com.novel.mapper.ChapterMapper;
import com.novel.mapper.UserMapper;
import com.novel.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private ChapterMapper chapterMapper;
    @Mock
    private BookService bookService;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    void updateBookStatusShouldReturnErrorWhenStatusInvalid() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthorId(100L);
        when(bookMapper.selectById(1L)).thenReturn(book);

        RestResp<Void> resp = authorService.updateBookStatus(100L, 1L, 3);

        assertEquals(500, resp.getCode());
        assertEquals("状态值无效", resp.getMsg());
        verify(bookMapper, never()).updateById(any(Book.class));
    }

    @Test
    void updateBookStatusShouldReturnErrorWhenNoPermission() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthorId(101L);
        when(bookMapper.selectById(1L)).thenReturn(book);

        RestResp<Void> resp = authorService.updateBookStatus(100L, 1L, 1);

        assertEquals(500, resp.getCode());
        assertEquals("无权操作此小说", resp.getMsg());
        verify(bookMapper, never()).updateById(any(Book.class));
    }

    @Test
    void updateBookStatusShouldSucceedWhenInputValid() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthorId(100L);
        book.setStatus(0);
        when(bookMapper.selectById(1L)).thenReturn(book);

        RestResp<Void> resp = authorService.updateBookStatus(100L, 1L, 2);

        assertEquals(200, resp.getCode());
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookMapper, times(1)).updateById(captor.capture());
        assertEquals(2, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getUpdateTime());
    }
}
