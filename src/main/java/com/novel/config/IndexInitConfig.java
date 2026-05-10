package com.novel.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IndexInitConfig implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        createAuthorUpdateTimeIndex();
    }

    private void createAuthorUpdateTimeIndex() {
        try {
            String sql = "CREATE INDEX IF NOT EXISTS idx_book_author_id_update_time ON book (author_id, update_time DESC)";
            jdbcTemplate.execute(sql);
            log.info("索引 idx_book_author_id_update_time 创建成功");
        } catch (Exception e) {
            log.warn("索引 idx_book_author_id_update_time 创建失败或已存在: {}", e.getMessage());
        }
    }
}