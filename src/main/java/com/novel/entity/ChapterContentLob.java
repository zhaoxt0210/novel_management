package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 章节内容大对象实体类
 * 用于存储章节的大文本内容
 */
@Data
@TableName("chapter_content_lob")
public class ChapterContentLob {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 章节ID
     */
    private Long chapterId;
    
    /**
     * 章节内容（大文本）
     */
    private String content;
    
    /**
     * 内容长度（字符数）
     */
    private Integer contentLength;
    
    /**
     * 字数统计
     */
    private Integer wordCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
