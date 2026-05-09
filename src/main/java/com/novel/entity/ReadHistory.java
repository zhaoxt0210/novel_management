<<<<<<< HEAD
package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("read_history")
public class ReadHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long bookId;
    private Long chapterId;
    private Integer chapterNum;
    private String chapterName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
=======
package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("read_history")
public class ReadHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long bookId;
    private Long chapterId;
    private Integer chapterNum;
    private String chapterName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
>>>>>>> f761e4fcf7d418a7792e50eeba7078e6fc32c340
}