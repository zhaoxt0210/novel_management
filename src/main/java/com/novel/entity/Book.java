<<<<<<< HEAD
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

    // 新增审核相关字段
    private Integer auditStatus;  // 审核状态: 0-草稿,1-待审核,2-已发布,3-已驳回
    private String auditRemark;    // 审核备注
    private LocalDateTime submitTime;  // 提交审核时间
    private LocalDateTime auditTime;   // 审核时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
=======
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

    // 新增审核相关字段
    private Integer auditStatus;  // 审核状态: 0-草稿,1-待审核,2-已发布,3-已驳回
    private String auditRemark;    // 审核备注
    private LocalDateTime submitTime;  // 提交审核时间
    private LocalDateTime auditTime;   // 审核时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}