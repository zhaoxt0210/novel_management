package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    @Update("UPDATE book SET visit_count = visit_count + 1 WHERE id = #{bookId}")
    void addVisitCount(@Param("bookId") Long bookId);
    
    @Update("UPDATE book SET favorite_count = favorite_count + 1 WHERE id = #{bookId}")
    void incrementFavoriteCount(@Param("bookId") Long bookId);
    
    @Update("UPDATE book SET favorite_count = favorite_count - 1 WHERE id = #{bookId} AND favorite_count > 0")
    void decrementFavoriteCount(@Param("bookId") Long bookId);
    
    @Select("SELECT * FROM book WHERE book_name LIKE CONCAT('%', #{keyword}, '%') OR author_name LIKE CONCAT('%', #{keyword}, '%')")
    List<Book> searchBooks(@Param("keyword") String keyword);
    
    @Select("SELECT * FROM book WHERE category_id = #{categoryId} ORDER BY ${sortBy} DESC LIMIT #{offset}, #{limit}")
    List<Book> getBooksByCategory(@Param("categoryId") Long categoryId, 
                                   @Param("sortBy") String sortBy,
                                   @Param("offset") Integer offset,
                                   @Param("limit") Integer limit);
    
    @Select("SELECT * FROM book WHERE author_id = #{authorId} ORDER BY update_time DESC")
    List<Book> selectByAuthorId(@Param("authorId") Long authorId);
    
    @Select("SELECT * FROM book WHERE status = 0 ORDER BY ${sortBy} DESC LIMIT #{limit}")
    List<Book> getRankingBooks(@Param("sortBy") String sortBy, @Param("limit") Integer limit);
}