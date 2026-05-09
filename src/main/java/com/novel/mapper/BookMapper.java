<<<<<<< HEAD
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

    @Update("UPDATE book b SET b.favorite_count = (SELECT COUNT(*) FROM book_favorite bf WHERE bf.book_id = #{bookId}) WHERE b.id = #{bookId}")
    void syncFavoriteCount(@Param("bookId") Long bookId);
    
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
=======
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

    @Update("UPDATE book b SET b.favorite_count = (SELECT COUNT(*) FROM book_favorite bf WHERE bf.book_id = #{bookId}) WHERE b.id = #{bookId}")
    void syncFavoriteCount(@Param("bookId") Long bookId);

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

    // ========== 推荐方法（修改版）==========

    /**
     * 最新推荐（不限制热度，方便测试）
     */
    @Select("SELECT * FROM book " +
            "WHERE audit_status = 2 " +
            "ORDER BY update_time DESC LIMIT #{limit}")
    List<Book> selectLatestRecommendSimple(@Param("limit") int limit);

    /**
     * 最新推荐：筛选更新时间最新，且热度(visit_count + favorite_count) > 10000 的作品
     */
    @Select("SELECT b.id, b.book_name, b.author_id, b.author_name, b.category_id, " +
            "b.cover, b.description, b.total_words, b.status, b.visit_count, b.favorite_count, " +
            "b.last_chapter_id, b.last_chapter_name, b.audit_status, b.audit_remark, " +
            "b.submit_time, b.audit_time, b.create_time, b.update_time, " +
            "(b.visit_count + b.favorite_count) AS hot_score " +
            "FROM book b " +
            "WHERE b.audit_status = 2 " +
            "AND (b.visit_count + b.favorite_count) > 10000 " +
            "ORDER BY b.update_time DESC LIMIT #{limit}")
    List<Book> selectLatestRecommend(@Param("limit") int limit);

    /**
     * 最热推荐：按热度(visit_count + favorite_count)倒序
     */
    @Select("SELECT b.id, b.book_name, b.author_id, b.author_name, b.category_id, " +
            "b.cover, b.description, b.total_words, b.status, b.visit_count, b.favorite_count, " +
            "b.last_chapter_id, b.last_chapter_name, b.audit_status, b.audit_remark, " +
            "b.submit_time, b.audit_time, b.create_time, b.update_time, " +
            "(b.visit_count + b.favorite_count) AS hot_score " +
            "FROM book b " +
            "WHERE b.audit_status = 2 " +
            "ORDER BY hot_score DESC LIMIT #{limit}")
    List<Book> selectHottestRecommend(@Param("limit") int limit);

    /**
     * 最热推荐（不限制审核状态，用于测试）
     */
    @Select("SELECT b.id, b.book_name, b.author_name, b.visit_count, b.favorite_count, " +
            "(b.visit_count + b.favorite_count) AS hot_score " +
            "FROM book b " +
            "ORDER BY hot_score DESC LIMIT #{limit}")
    List<Book> selectHottestRecommendTest(@Param("limit") int limit);

    /**
     * 收藏推荐：按 favorite_count 倒序
     */
    @Select("SELECT * FROM book " +
            "WHERE audit_status = 2 " +
            "ORDER BY favorite_count DESC LIMIT #{limit}")
    List<Book> selectMostFavoritedRecommend(@Param("limit") int limit);

    /**
     * 收藏推荐（不限制审核状态，用于测试）
     */
    @Select("SELECT id, book_name, author_name, favorite_count FROM book " +
            "ORDER BY favorite_count DESC LIMIT #{limit}")
    List<Book> selectMostFavoritedRecommendTest(@Param("limit") int limit);
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}