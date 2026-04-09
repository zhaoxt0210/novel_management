package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("author_apply")
public class AuthorApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String idCard;
    private String phone;
    private String reason;
    private Integer status;
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}