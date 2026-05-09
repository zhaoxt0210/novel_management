<<<<<<< HEAD
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
    private Integer auditStatus;  // 审核状态: 0-草稿,1-待审核,2-已发布,3-已驳回
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
=======
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
    private Integer auditStatus;  // 审核状态: 0-草稿,1-待审核,2-已发布,3-已驳回
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}