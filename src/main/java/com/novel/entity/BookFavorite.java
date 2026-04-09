package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_favorite")
public class BookFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long bookId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private Long lastReadChapterId;
    private Integer lastReadChapterNum;
    private String lastReadChapterName;
    private Integer readProgress;
    private LocalDateTime lastReadTime;
}