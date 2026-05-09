-- ============================================
-- 数据库存储过程和函数创建脚本
-- 用于封装复杂的业务逻辑
-- ============================================

DELIMITER //

-- ============================================
-- 存储过程：获取书籍详情（包含统计信息）
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_get_book_detail(
    IN p_book_id BIGINT
)
BEGIN
    SELECT 
        b.*,
        c.category_name,
        (SELECT COUNT(*) FROM chapter ch WHERE ch.book_id = b.id) AS chapter_count,
        (SELECT COUNT(*) FROM comment cm WHERE cm.book_id = b.id) AS comment_count,
        (SELECT COALESCE(AVG(rating), 0) FROM comment cm WHERE cm.book_id = b.id) AS avg_rating
    FROM book b
    LEFT JOIN category c ON b.category_id = c.id
    WHERE b.id = p_book_id;
END //

-- ============================================
-- 存储过程：获取用户阅读统计
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_get_user_reading_stats(
    IN p_user_id BIGINT
)
BEGIN
    SELECT 
        u.id AS user_id,
        u.username,
        u.nickname,
        COUNT(DISTINCT rh.book_id) AS books_read_count,
        COUNT(rh.id) AS total_read_records,
        COALESCE(SUM(rh.read_count), 0) AS total_read_chapters,
        MAX(rh.update_time) AS last_read_time,
        (SELECT COUNT(*) FROM book_favorite bf WHERE bf.user_id = p_user_id) AS favorite_count
    FROM user u
    LEFT JOIN read_history rh ON u.id = rh.user_id
    WHERE u.id = p_user_id
    GROUP BY u.id, u.username, u.nickname;
END //

-- ============================================
-- 存储过程：获取书籍排行榜
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_get_book_ranking(
    IN p_rank_type VARCHAR(20),
    IN p_period VARCHAR(20),
    IN p_limit INT
)
BEGIN
    IF p_rank_type = 'visit' THEN
        SELECT 
            b.id,
            b.book_name,
            b.author_name,
            b.cover,
            b.visit_count AS score,
            RANK() OVER (ORDER BY b.visit_count DESC) AS rank_num
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2
        ORDER BY b.visit_count DESC
        LIMIT p_limit;
    ELSEIF p_rank_type = 'favorite' THEN
        SELECT 
            b.id,
            b.book_name,
            b.author_name,
            b.cover,
            b.favorite_count AS score,
            RANK() OVER (ORDER BY b.favorite_count DESC) AS rank_num
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2
        ORDER BY b.favorite_count DESC
        LIMIT p_limit;
    ELSEIF p_rank_type = 'rating' THEN
        SELECT 
            b.id,
            b.book_name,
            b.author_name,
            b.cover,
            b.rating AS score,
            RANK() OVER (ORDER BY b.rating DESC) AS rank_num
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2 AND b.rating > 0
        ORDER BY b.rating DESC
        LIMIT p_limit;
    ELSEIF p_rank_type = 'new' THEN
        SELECT 
            b.id,
            b.book_name,
            b.author_name,
            b.cover,
            b.create_time AS score,
            RANK() OVER (ORDER BY b.create_time DESC) AS rank_num
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2
        ORDER BY b.create_time DESC
        LIMIT p_limit;
    ELSE
        SELECT 
            b.id,
            b.book_name,
            b.author_name,
            b.cover,
            (b.visit_count * 0.5 + b.favorite_count * 0.3 + COALESCE(b.rating, 0) * 100 * 0.2) AS score,
            RANK() OVER (ORDER BY (b.visit_count * 0.5 + b.favorite_count * 0.3 + COALESCE(b.rating, 0) * 100 * 0.2) DESC) AS rank_num
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2
        ORDER BY score DESC
        LIMIT p_limit;
    END IF;
END //

-- ============================================
-- 存储过程：更新书籍排行榜
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_update_book_ranking(
    IN p_rank_type VARCHAR(20),
    IN p_period VARCHAR(20)
)
BEGIN
    DECLARE v_period_start DATETIME;
    
    -- 计算时间段起始时间
    IF p_period = 'day' THEN
        SET v_period_start = DATE_SUB(NOW(), INTERVAL 1 DAY);
    ELSEIF p_period = 'week' THEN
        SET v_period_start = DATE_SUB(NOW(), INTERVAL 1 WEEK);
    ELSEIF p_period = 'month' THEN
        SET v_period_start = DATE_SUB(NOW(), INTERVAL 1 MONTH);
    ELSE
        SET v_period_start = DATE_SUB(NOW(), INTERVAL 1 YEAR);
    END IF;
    
    -- 删除旧的排行榜数据
    DELETE FROM book_rank WHERE rank_type = p_rank_type AND period = p_period;
    
    -- 插入新的排行榜数据
    IF p_rank_type = 'visit' THEN
        INSERT INTO book_rank (book_id, rank_type, period, rank_num, score, create_time)
        SELECT 
            b.id,
            p_rank_type,
            p_period,
            ROW_NUMBER() OVER (ORDER BY b.visit_count DESC),
            b.visit_count,
            NOW()
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2
        ORDER BY b.visit_count DESC
        LIMIT 100;
    ELSEIF p_rank_type = 'favorite' THEN
        INSERT INTO book_rank (book_id, rank_type, period, rank_num, score, create_time)
        SELECT 
            b.id,
            p_rank_type,
            p_period,
            ROW_NUMBER() OVER (ORDER BY b.favorite_count DESC),
            b.favorite_count,
            NOW()
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2
        ORDER BY b.favorite_count DESC
        LIMIT 100;
    ELSEIF p_rank_type = 'rating' THEN
        INSERT INTO book_rank (book_id, rank_type, period, rank_num, score, create_time)
        SELECT 
            b.id,
            p_rank_type,
            p_period,
            ROW_NUMBER() OVER (ORDER BY b.rating DESC),
            b.rating,
            NOW()
        FROM book b
        WHERE b.status = 1 AND b.audit_status = 2 AND b.rating > 0
        ORDER BY b.rating DESC
        LIMIT 100;
    END IF;
END //

-- ============================================
-- 存储过程：搜索书籍
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_search_books(
    IN p_keyword VARCHAR(255),
    IN p_category_id BIGINT,
    IN p_status INT,
    IN p_sort_field VARCHAR(20),
    IN p_sort_order VARCHAR(10),
    IN p_page_num INT,
    IN p_page_size INT
)
BEGIN
    DECLARE v_offset INT;
    SET v_offset = (p_page_num - 1) * p_page_size;
    
    SELECT 
        b.*,
        c.category_name,
        (SELECT COUNT(*) FROM chapter ch WHERE ch.book_id = b.id) AS chapter_count
    FROM book b
    LEFT JOIN category c ON b.category_id = c.id
    WHERE b.status = COALESCE(p_status, b.status)
      AND (p_category_id IS NULL OR b.category_id = p_category_id)
      AND (p_keyword IS NULL OR b.book_name LIKE CONCAT('%', p_keyword, '%') OR b.author_name LIKE CONCAT('%', p_keyword, '%'))
    ORDER BY 
        CASE WHEN p_sort_field = 'create_time' AND p_sort_order = 'desc' THEN b.create_time END DESC,
        CASE WHEN p_sort_field = 'create_time' AND p_sort_order = 'asc' THEN b.create_time END ASC,
        CASE WHEN p_sort_field = 'visit_count' AND p_sort_order = 'desc' THEN b.visit_count END DESC,
        CASE WHEN p_sort_field = 'visit_count' AND p_sort_order = 'asc' THEN b.visit_count END ASC,
        CASE WHEN p_sort_field = 'favorite_count' AND p_sort_order = 'desc' THEN b.favorite_count END DESC,
        CASE WHEN p_sort_field = 'favorite_count' AND p_sort_order = 'asc' THEN b.favorite_count END ASC,
        CASE WHEN p_sort_field = 'rating' AND p_sort_order = 'desc' THEN b.rating END DESC,
        CASE WHEN p_sort_field = 'rating' AND p_sort_order = 'asc' THEN b.rating END ASC,
        b.id DESC
    LIMIT p_page_size OFFSET v_offset;
END //

-- ============================================
-- 存储过程：清理过期阅读历史
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_clean_expired_read_history(
    IN p_days INT
)
BEGIN
    DECLARE v_deleted_count INT DEFAULT 0;
    
    DELETE FROM read_history 
    WHERE update_time < DATE_SUB(NOW(), INTERVAL p_days DAY);
    
    SET v_deleted_count = ROW_COUNT();
    
    SELECT v_deleted_count AS deleted_count;
END //

-- ============================================
-- 存储过程：批量审核书籍
-- ============================================
CREATE PROCEDURE IF NOT EXISTS sp_batch_audit_books(
    IN p_book_ids TEXT,
    IN p_audit_status INT,
    IN p_audit_remark VARCHAR(500)
)
BEGIN
    -- 更新书籍审核状态
    UPDATE book 
    SET audit_status = p_audit_status,
        audit_remark = p_audit_remark,
        audit_time = NOW(),
        update_time = NOW()
    WHERE FIND_IN_SET(id, p_book_ids) > 0;
    
    SELECT ROW_COUNT() AS updated_count;
END //

-- ============================================
-- 函数：计算书籍阅读进度百分比
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_calc_read_progress(
    p_book_id BIGINT,
    p_chapter_id BIGINT
) RETURNS DECIMAL(5,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total_chapters INT;
    DECLARE v_current_chapter_num INT;
    DECLARE v_progress DECIMAL(5,2);
    
    -- 获取总章节数
    SELECT COUNT(*) INTO v_total_chapters
    FROM chapter WHERE book_id = p_book_id;
    
    -- 获取当前章节序号
    SELECT chapter_num INTO v_current_chapter_num
    FROM chapter WHERE id = p_chapter_id;
    
    -- 计算进度
    IF v_total_chapters > 0 THEN
        SET v_progress = ROUND(v_current_chapter_num * 100.0 / v_total_chapters, 2);
    ELSE
        SET v_progress = 0;
    END IF;
    
    RETURN v_progress;
END //

-- ============================================
-- 函数：获取书籍总字数
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_get_book_word_count(
    p_book_id BIGINT
) RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_word_count INT;
    
    SELECT COALESCE(SUM(word_count), 0) INTO v_word_count
    FROM chapter 
    WHERE book_id = p_book_id AND status = 1;
    
    RETURN v_word_count;
END //

-- ============================================
-- 函数：检查用户是否已收藏书籍
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_is_book_favorited(
    p_user_id BIGINT,
    p_book_id BIGINT
) RETURNS TINYINT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_count INT;
    
    SELECT COUNT(*) INTO v_count
    FROM book_favorite 
    WHERE user_id = p_user_id AND book_id = p_book_id;
    
    RETURN IF(v_count > 0, 1, 0);
END //

-- ============================================
-- 函数：获取用户阅读时长（分钟）
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_get_user_reading_minutes(
    p_user_id BIGINT,
    p_start_date DATE,
    p_end_date DATE
) RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_minutes INT;
    
    SELECT COALESCE(SUM(read_count * 5), 0) INTO v_minutes  -- 假设每章平均阅读5分钟
    FROM read_history 
    WHERE user_id = p_user_id 
      AND DATE(update_time) BETWEEN p_start_date AND p_end_date;
    
    RETURN v_minutes;
END //

-- ============================================
-- 函数：格式化字数（转换为万字/千字）
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_format_word_count(
    p_word_count INT
) RETURNS VARCHAR(20)
DETERMINISTIC
NO SQL
BEGIN
    IF p_word_count >= 10000 THEN
        RETURN CONCAT(ROUND(p_word_count / 10000, 2), '万字');
    ELSEIF p_word_count >= 1000 THEN
        RETURN CONCAT(ROUND(p_word_count / 1000, 1), '千字');
    ELSE
        RETURN CONCAT(p_word_count, '字');
    END IF;
END //

-- ============================================
-- 函数：计算书籍热度分数
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_calc_book_heat_score(
    p_visit_count BIGINT,
    p_favorite_count BIGINT,
    p_rating DECIMAL(3,2)
) RETURNS DECIMAL(10,2)
DETERMINISTIC
NO SQL
BEGIN
    RETURN p_visit_count * 0.5 + p_favorite_count * 0.3 + COALESCE(p_rating, 0) * 100 * 0.2;
END //

-- ============================================
-- 函数：获取用户等级名称
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_get_user_level_name(
    p_read_count INT
) RETURNS VARCHAR(20)
DETERMINISTIC
NO SQL
BEGIN
    RETURN CASE
        WHEN p_read_count >= 10000 THEN '书仙'
        WHEN p_read_count >= 5000 THEN '书圣'
        WHEN p_read_count >= 2000 THEN '书王'
        WHEN p_read_count >= 1000 THEN '书痴'
        WHEN p_read_count >= 500 THEN '书迷'
        WHEN p_read_count >= 100 THEN '书友'
        ELSE '新手'
    END;
END //

-- ============================================
-- 函数：生成唯一订单号
-- ============================================
CREATE FUNCTION IF NOT EXISTS fn_generate_order_no(
    p_prefix VARCHAR(10)
) RETURNS VARCHAR(32)
DETERMINISTIC
NO SQL
BEGIN
    RETURN CONCAT(
        p_prefix,
        DATE_FORMAT(NOW(), '%Y%m%d'),
        LPAD(FLOOR(RAND() * 1000000), 6, '0')
    );
END //

DELIMITER ;
