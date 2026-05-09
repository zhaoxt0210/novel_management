package com.novel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.novel.entity.BookCoverBlob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍封面图片大对象Mapper
 */
@Mapper
public interface BookCoverBlobMapper extends BaseMapper<BookCoverBlob> {
}
