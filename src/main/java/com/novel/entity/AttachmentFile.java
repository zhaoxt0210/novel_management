package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 附件文件实体类
 * 用于存储通用文件数据（大对象）
 */
@Data
@TableName("attachment_file")
public class AttachmentFile {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 原始文件名
     */
    private String fileName;
    
    /**
     * 文件MIME类型
     */
    private String fileType;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件二进制数据（大对象）
     */
    @TableField(exist = true)
    private byte[] fileData;
    
    /**
     * 文件MD5哈希值（用于去重）
     */
    private String fileHash;
    
    /**
     * 关联类型：book/chapter/user等
     */
    private String refType;
    
    /**
     * 关联ID
     */
    private Long refId;
    
    /**
     * 上传用户ID
     */
    private Long uploadUserId;
    
    /**
     * 存储路径（如果使用文件系统存储）
     */
    private String storagePath;
    
    /**
     * 存储类型：1-数据库存储，2-文件系统存储
     */
    private Integer storageType;
    
    /**
     * 下载次数
     */
    private Integer downloadCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
