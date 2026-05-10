package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {
    
    @Select("SELECT id FROM chapter WHERE book_id = #{bookId} AND chapter_num < #{chapterNum} ORDER BY chapter_num DESC LIMIT 1")
    Long getPrevChapterId(@Param("bookId") Long bookId, @Param("chapterNum") Integer chapterNum);
    
    @Select("SELECT id FROM chapter WHERE book_id = #{bookId} AND chapter_num > #{chapterNum} ORDER BY chapter_num ASC LIMIT 1")
    Long getNextChapterId(@Param("bookId") Long bookId, @Param("chapterNum") Integer chapterNum);

    @Update("UPDATE chapter SET audit_status = #{auditStatus}, update_time = #{updateTime} WHERE book_id = #{bookId}")
    void batchUpdateAuditStatus(@Param("bookId") Long bookId, 
                                @Param("auditStatus") Integer auditStatus, 
                                @Param("updateTime") LocalDateTime updateTime);
}