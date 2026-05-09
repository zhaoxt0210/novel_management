package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 书籍封面图片大对象实体类
 * 用于存储书籍封面的二进制图片数据
 */
@Data
@TableName("book_cover_blob")
public class BookCoverBlob {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 书籍ID
     */
    private Long bookId;
    
    /**
     * 封面图片二进制数据（大对象）
     */
    @TableField(exist = true)
    private byte[] coverData;
    
    /**
     * 图片类型
     */
    private String coverType;
    
    /**
     * 文件大小（字节）
     */
    private Integer fileSize;
    
    /**
     * 图片宽度
     */
    private Integer width;
    
    /**
     * 图片高度
     */
    private Integer height;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
