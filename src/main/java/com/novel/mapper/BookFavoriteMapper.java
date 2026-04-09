package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.BookFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BookFavoriteMapper extends BaseMapper<BookFavorite> {
    
    @Select("SELECT COUNT(*) FROM book_favorite WHERE book_id = #{bookId}")
    Long countByBookId(@Param("bookId") Long bookId);
    
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} ORDER BY bf.create_time DESC")
    List<BookFavorite> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} ORDER BY bf.last_read_time DESC")
    List<BookFavorite> selectByUserIdOrderByLastRead(@Param("userId") Long userId);
    
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} ORDER BY bf.read_progress DESC")
    List<BookFavorite> selectByUserIdOrderByProgress(@Param("userId") Long userId);
    
    @Select("SELECT * FROM book_favorite WHERE user_id = #{userId} AND book_id = #{bookId}")
    BookFavorite selectByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    @Update("UPDATE book_favorite SET last_read_chapter_id = #{chapterId}, last_read_chapter_num = #{chapterNum}, " +
            "last_read_chapter_name = #{chapterName}, read_progress = #{progress}, last_read_time = NOW() " +
            "WHERE user_id = #{userId} AND book_id = #{bookId}")
    void updateReadProgress(@Param("userId") Long userId, @Param("bookId") Long bookId,
                           @Param("chapterId") Long chapterId, @Param("chapterNum") Integer chapterNum,
                           @Param("chapterName") String chapterName, @Param("progress") Integer progress);
}