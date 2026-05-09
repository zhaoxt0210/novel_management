package com.novel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统配置大对象实体类
 * 用于存储大文本/JSON等配置数据
 */
@Data
@TableName("system_config_lob")
public class SystemConfigLob {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 配置键
     */
    private String configKey;
    
    /**
     * 配置名称
     */
    private String configName;
    
    /**
     * 配置值（大文本/JSON）
     */
    private String configValue;
    
    /**
     * 配置类型：text/json/xml/html
     */
    private String configType;
    
    /**
     * 配置说明
     */
    private String description;
    
    /**
     * 是否系统配置：0-否，1-是
     */
    private Integer isSystem;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
