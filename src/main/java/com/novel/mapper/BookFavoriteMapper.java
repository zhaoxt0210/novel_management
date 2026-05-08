package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.BookFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookFavoriteMapper extends BaseMapper<BookFavorite> {

    @Select("SELECT COUNT(*) FROM book_favorite WHERE book_id = #{bookId}")
    Long countByBookId(@Param("bookId") Long bookId);

    // 按类型统计
    @Select("SELECT COUNT(*) FROM book_favorite WHERE book_id = #{bookId} AND type = #{type}")
    Long countByBookIdAndType(@Param("bookId") Long bookId, @Param("type") Integer type);

    @Select({
            "<script>",
            "SELECT bf.book_id AS bookId, COUNT(*) AS favoriteCount",
            "FROM book_favorite bf",
            "WHERE bf.book_id IN",
            "<foreach collection='bookIds' item='bookId' open='(' separator=',' close=')'>",
            "#{bookId}",
            "</foreach>",
            "AND bf.type = 2",
            "GROUP BY bf.book_id",
            "</script>"
    })
    List<Map<String, Object>> countByBookIds(@Param("bookIds") List<Long> bookIds);

    // 获取用户书架（type = 1）
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} AND bf.type = 1 ORDER BY bf.create_time DESC")
    List<BookFavorite> selectBookshelfByUserId(@Param("userId") Long userId);

    // 获取用户书架按最后阅读排序
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} AND bf.type = 1 ORDER BY bf.last_read_time DESC")
    List<BookFavorite> selectBookshelfByUserIdOrderByLastRead(@Param("userId") Long userId);

    // 获取用户书架按阅读进度排序
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} AND bf.type = 1 ORDER BY bf.read_progress DESC")
    List<BookFavorite> selectBookshelfByUserIdOrderByProgress(@Param("userId") Long userId);

    // 获取用户收藏（type = 2）
    @Select("SELECT bf.* FROM book_favorite bf WHERE bf.user_id = #{userId} AND bf.type = 2 ORDER BY bf.create_time DESC")
    List<BookFavorite> selectFavoritesByUserId(@Param("userId") Long userId);

    // 检查是否在书架中（type = 1）
    @Select("SELECT * FROM book_favorite WHERE user_id = #{userId} AND book_id = #{bookId} AND type = 1")
    BookFavorite selectBookshelfByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 检查是否已收藏（type = 2）
    @Select("SELECT * FROM book_favorite WHERE user_id = #{userId} AND book_id = #{bookId} AND type = 2")
    BookFavorite selectFavoriteByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // 更新阅读进度（书架专用）
    @Update("UPDATE book_favorite SET last_read_chapter_id = #{chapterId}, last_read_chapter_num = #{chapterNum}, " +
            "last_read_chapter_name = #{chapterName}, read_progress = #{progress}, last_read_time = NOW() " +
            "WHERE user_id = #{userId} AND book_id = #{bookId} AND type = 1")
    void updateReadProgress(@Param("userId") Long userId, @Param("bookId") Long bookId,
                            @Param("chapterId") Long chapterId, @Param("chapterNum") Integer chapterNum,
                            @Param("chapterName") String chapterName, @Param("progress") Integer progress);
}