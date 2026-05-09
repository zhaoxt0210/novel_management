package com.novel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列配置类
 * 用于管理数据库序列的配置信息
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "novel.sequence")
public class SequenceConfig {
    
    /**
     * 是否启用序列
     */
    private boolean enabled = true;
    
    /**
     * 序列缓存大小
     */
    private int cacheSize = 1000;
    
    /**
     * 各表序列起始值配置
     */
    private Map<String, Long> startValues = new HashMap<>();
    
    public SequenceConfig() {
        // 默认起始值配置
        startValues.put("user", 10000L);
        startValues.put("book", 10000L);
        startValues.put("chapter", 100000L);
        startValues.put("category", 100L);
        startValues.put("comment", 100000L);
        startValues.put("book_favorite", 10000L);
        startValues.put("read_history", 100000L);
        startValues.put("author_apply", 1000L);
        startValues.put("book_rank", 1000L);
        startValues.put("admin", 100L);
    }
    
    /**
     * 获取指定表的序列起始值
     */
    public long getStartValue(String tableName) {
        return startValues.getOrDefault(tableName, 10000L);
    }
}
