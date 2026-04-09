package com.novel.service;

import com.novel.common.resp.RestResp;
import com.novel.dto.resp.BookInfoRespDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    public void testListHomeBooks() {
        RestResp<List<BookInfoRespDto>> result = bookService.listHomeBooks();
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetRanking() {
        RestResp<List<BookInfoRespDto>> result = bookService.getRanking("visit", 10);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetRankingByFavorite() {
        RestResp<List<BookInfoRespDto>> result = bookService.getRanking("favorite", 10);
        
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }
}
