package com.novel.service;

import com.novel.dto.req.BookSearchReqDto;
import com.novel.dto.resp.BookInfoRespDto;
import com.novel.entity.Book;
import com.novel.entity.Category;
import com.novel.mapper.BookMapper;
import com.novel.mapper.CategoryMapper;
import com.novel.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BookServiceSearchTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private List<Book> mockBooks;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        Category category = new Category();
        category.setId(1L);
        category.setName("古典文学");
        when(categoryMapper.selectById(1L)).thenReturn(category);
        
        mockBooks = new ArrayList<>();
        
        Book book1 = new Book();
        book1.setId(1L);
        book1.setBookName("红楼梦");
        book1.setAuthorName("曹雪芹");
        book1.setCategoryId(1L);
        book1.setStatus(0);
        mockBooks.add(book1);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setBookName("三国演义");
        book2.setAuthorName("罗贯中");
        book2.setCategoryId(1L);
        book2.setStatus(0);
        mockBooks.add(book2);

        Book book3 = new Book();
        book3.setId(3L);
        book3.setBookName("水浒传");
        book3.setAuthorName("施耐庵");
        book3.setCategoryId(1L);
        book3.setStatus(0);
        mockBooks.add(book3);

        Book book4 = new Book();
        book4.setId(4L);
        book4.setBookName("西游记");
        book4.setAuthorName("吴承恩");
        book4.setCategoryId(1L);
        book4.setStatus(0);
        mockBooks.add(book4);

        Book book5 = new Book();
        book5.setId(5L);
        book5.setBookName("金瓶梅");
        book5.setAuthorName("兰陵笑笑生");
        book5.setCategoryId(1L);
        book5.setStatus(0);
        mockBooks.add(book5);

        Book book6 = new Book();
        book6.setId(6L);
        book6.setBookName("红岩");
        book6.setAuthorName("罗广斌");
        book6.setCategoryId(1L);
        book6.setStatus(0);
        mockBooks.add(book6);
    }

    @Test
    @DisplayName("单个汉字搜索 - 小说名中包含该汉字")
    void testSingleCharacterSearch_BookName() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("红");
        dto.setPageNum(1);
        dto.setPageSize(20);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(0)); // 红楼梦
        expectedBooks.add(mockBooks.get(5)); // 红岩

        when(bookMapper.searchBooks("红")).thenReturn(expectedBooks);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertNotNull(data);
        assertEquals(2, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(b -> "红楼梦".equals(b.getBookName())));
        assertTrue(list.stream().anyMatch(b -> "红岩".equals(b.getBookName())));
    }

    @Test
    @DisplayName("单个汉字搜索 - 作家名中包含该汉字")
    void testSingleCharacterSearch_AuthorName() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("罗");
        dto.setPageNum(1);
        dto.setPageSize(20);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(1)); // 三国演义 - 罗贯中
        expectedBooks.add(mockBooks.get(5)); // 红岩 - 罗广斌

        when(bookMapper.searchBooks("罗")).thenReturn(expectedBooks);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(2, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(b -> "三国演义".equals(b.getBookName())));
        assertTrue(list.stream().anyMatch(b -> "红岩".equals(b.getBookName())));
    }

    @Test
    @DisplayName("部分匹配搜索 - 小说名部分匹配")
    void testPartialMatchSearch_BookName() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("三国");
        dto.setPageNum(1);
        dto.setPageSize(20);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(1)); // 三国演义

        when(bookMapper.searchBooks("三国")).thenReturn(expectedBooks);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(1, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertEquals(1, list.size());
        assertEquals("三国演义", list.get(0).getBookName());
    }

    @Test
    @DisplayName("部分匹配搜索 - 作家名部分匹配")
    void testPartialMatchSearch_AuthorName() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("贯中");
        dto.setPageNum(1);
        dto.setPageSize(20);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(1)); // 三国演义 - 罗贯中

        when(bookMapper.searchBooks("贯中")).thenReturn(expectedBooks);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(1, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertEquals(1, list.size());
        assertEquals("罗贯中", list.get(0).getAuthorName());
    }

    @Test
    @DisplayName("完整匹配搜索 - 精确匹配小说名")
    void testExactMatchSearch_BookName() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("红楼梦");
        dto.setPageNum(1);
        dto.setPageSize(20);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(0)); // 红楼梦

        when(bookMapper.searchBooks("红楼梦")).thenReturn(expectedBooks);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(1, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertEquals(1, list.size());
        assertEquals("红楼梦", list.get(0).getBookName());
    }

    @Test
    @DisplayName("完整匹配搜索 - 精确匹配作家名")
    void testExactMatchSearch_AuthorName() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("曹雪芹");
        dto.setPageNum(1);
        dto.setPageSize(20);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(0)); // 红楼梦 - 曹雪芹

        when(bookMapper.searchBooks("曹雪芹")).thenReturn(expectedBooks);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(1, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertEquals(1, list.size());
        assertEquals("曹雪芹", list.get(0).getAuthorName());
    }

    @Test
    @DisplayName("空关键词搜索 - 返回空结果")
    void testEmptyKeywordSearch() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("");
        dto.setPageNum(1);
        dto.setPageSize(20);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(0, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("null关键词搜索 - 返回空结果")
    void testNullKeywordSearch() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword(null);
        dto.setPageNum(1);
        dto.setPageSize(20);

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(0, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("分页搜索 - 多页数据")
    void testPaginationSearch() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("国");
        dto.setPageNum(2);
        dto.setPageSize(2);

        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(mockBooks.get(1)); // 三国演义
        expectedBooks.add(mockBooks.get(3)); // 西游记 - 吴承恩（不匹配）
        
        when(bookMapper.searchBooks("国")).thenReturn(mockBooks.subList(0, 4));

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(4, data.get("total"));
        assertEquals(2, data.get("pageNum"));
        assertEquals(2, data.get("pageSize"));
    }

    @Test
    @DisplayName("无结果搜索 - 返回空结果")
    void testNoResultSearch() {
        BookSearchReqDto dto = new BookSearchReqDto();
        dto.setKeyword("不存在的书名");
        dto.setPageNum(1);
        dto.setPageSize(20);

        when(bookMapper.searchBooks("不存在的书名")).thenReturn(new ArrayList<>());

        var result = bookService.searchBooks(dto);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        Map<String, Object> data = result.getData();
        assertEquals(0, data.get("total"));
        @SuppressWarnings("unchecked")
        List<BookInfoRespDto> list = (List<BookInfoRespDto>) data.get("list");
        assertTrue(list.isEmpty());
    }
}