package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chapter")
public class Chapter {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Integer chapterNum;
    private String chapterName;
    private String content;
    private Integer wordCount;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}