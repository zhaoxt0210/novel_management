package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String bookName;
    private Long categoryId;
    private Long authorId;
    private String authorName;
    private String cover;
    private String description;
    private LocalDate publicationDate;
    private Integer totalWords;
    private BigDecimal rating;
    private Integer status;
    private Long visitCount;
    private Long favoriteCount;
    private Long lastChapterId;
    private String lastChapterName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}