package com.novel.common.util;

import com.novel.config.SequenceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列生成器
 * 用于生成数据库表的唯一ID
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SequenceGenerator {
    
    private final JdbcTemplate jdbcTemplate;
    private final SequenceConfig sequenceConfig;
    
    // 本地缓存的序列值
    private final ConcurrentHashMap<String, SequenceRange> sequenceCache = new ConcurrentHashMap<>();
    
    /**
     * 序列范围类
     */
    private static class SequenceRange {
        private final AtomicLong currentValue;
        private final long maxValue;
        
        public SequenceRange(long startValue, long cacheSize) {
            this.currentValue = new AtomicLong(startValue);
            this.maxValue = startValue + cacheSize - 1;
        }
        
        public Long nextValue() {
            long value = currentValue.getAndIncrement();
            return value <= maxValue ? value : null;
        }
    }
    
    /**
     * 获取下一个序列值
     *
     * @param sequenceName 序列名称
     * @return 下一个序列值
     */
    public synchronized Long nextValue(String sequenceName) {
        if (!sequenceConfig.isEnabled()) {
            return null;
        }
        
        SequenceRange range = sequenceCache.get(sequenceName);
        
        if (range == null) {
            // 首次获取，从数据库申请一批序列值
            range = fetchNewRange(sequenceName);
            sequenceCache.put(sequenceName, range);
        }
        
        Long value = range.nextValue();
        
        if (value == null) {
            // 当前范围已用完，申请新的范围
            range = fetchNewRange(sequenceName);
            sequenceCache.put(sequenceName, range);
            value = range.nextValue();
        }
        
        return value;
    }
    
    /**
     * 从数据库获取新的序列范围
     */
    private SequenceRange fetchNewRange(String sequenceName) {
        try {
            // 查询当前序列值
            Long currentValue = jdbcTemplate.queryForObject(
                "SELECT NEXTVAL(?)", 
                Long.class, 
                "seq_" + sequenceName + "_id"
            );
            
            if (currentValue == null) {
                throw new RuntimeException("Failed to get sequence value for: " + sequenceName);
            }
            
            return new SequenceRange(currentValue, sequenceConfig.getCacheSize());
            
        } catch (Exception e) {
            log.error("Failed to fetch sequence range for: {}", sequenceName, e);
            // 如果序列不存在，使用表名获取起始值
            String tableName = sequenceName.replace("seq_", "").replace("_id", "");
            long startValue = sequenceConfig.getStartValue(tableName);
            return new SequenceRange(startValue, sequenceConfig.getCacheSize());
        }
    }
    
    /**
     * 获取用户ID序列值
     */
    public Long nextUserId() {
        return nextValue("user");
    }
    
    /**
     * 获取书籍ID序列值
     */
    public Long nextBookId() {
        return nextValue("book");
    }
    
    /**
     * 获取章节ID序列值
     */
    public Long nextChapterId() {
        return nextValue("chapter");
    }
    
    /**
     * 获取分类ID序列值
     */
    public Long nextCategoryId() {
        return nextValue("category");
    }
    
    /**
     * 获取评论ID序列值
     */
    public Long nextCommentId() {
        return nextValue("comment");
    }
    
    /**
     * 获取书架ID序列值
     */
    public Long nextBookFavoriteId() {
        return nextValue("book_favorite");
    }
    
    /**
     * 获取阅读历史ID序列值
     */
    public Long nextReadHistoryId() {
        return nextValue("read_history");
    }
    
    /**
     * 获取作者申请ID序列值
     */
    public Long nextAuthorApplyId() {
        return nextValue("author_apply");
    }
    
    /**
     * 获取排行榜ID序列值
     */
    public Long nextBookRankId() {
        return nextValue("book_rank");
    }
    
    /**
     * 获取管理员ID序列值
     */
    public Long nextAdminId() {
        return nextValue("admin");
    }
    
    /**
     * 重置序列缓存（用于测试或特殊情况）
     */
    public void clearCache() {
        sequenceCache.clear();
        log.info("Sequence cache cleared");
    }
}
