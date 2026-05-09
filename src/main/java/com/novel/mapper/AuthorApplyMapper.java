package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.AuthorApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthorApplyMapper extends BaseMapper<AuthorApply> {
    
    @Select("SELECT * FROM author_apply WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT 1")
    AuthorApply findLatestByUserId(Long userId);
}