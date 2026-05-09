<<<<<<< HEAD
package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.ReadHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReadHistoryMapper extends BaseMapper<ReadHistory> {
    
    @Select("SELECT * FROM read_history WHERE user_id = #{userId} AND book_id = #{bookId} ORDER BY create_time DESC LIMIT 1")
    ReadHistory findLatestByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
=======
package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.ReadHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReadHistoryMapper extends BaseMapper<ReadHistory> {
    
    @Select("SELECT * FROM read_history WHERE user_id = #{userId} AND book_id = #{bookId} ORDER BY create_time DESC LIMIT 1")
    ReadHistory findLatestByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}