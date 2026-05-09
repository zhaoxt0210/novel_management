-- ============================================
-- 数据库视图创建脚本
-- 创建常用查询视图以简化复杂查询
-- ============================================

-- 1. 书籍详情视图（包含分类名称和作者信息）
CREATE OR REPLACE VIEW v_book_detail AS
SELECT 
    b.id,
    b.book_name,
    b.category_id,
    c.category_name,
    b.author_id,
    b.author_name,
    b.cover,
    b.description,
    b.publication_date,
    b.total_words,
    b.rating,
    b.status,
    b.visit_count,
    b.favorite_count,
    b.last_chapter_id,
    b.last_chapter_name,
    b.audit_status,
    b.audit_remark,
    b.submit_time,
    b.audit_time,
    b.create_time,
    b.update_time,
    (SELECT COUNT(*) FROM chapter ch WHERE ch.book_id = b.id) AS chapter_count
FROM book b
LEFT JOIN category c ON b.category_id = c.id;

-- 2. 用户阅读统计视图
CREATE OR REPLACE VIEW v_user_reading_stats AS
SELECT 
    u.id AS user_id,
    u.username,
    u.nickname,
    COUNT(DISTINCT rh.book_id) AS books_read_count,
    COUNT(rh.id) AS total_read_records,
    SUM(rh.read_count) AS total_read_chapters,
    MAX(rh.update_time) AS last_read_time
FROM user u
LEFT JOIN read_history rh ON u.id = rh.user_id
GROUP BY u.id, u.username, u.nickname;

-- 3. 书籍热度排行视图
CREATE OR REPLACE VIEW v_book_popularity AS
SELECT 
    b.id,
    b.book_name,
    b.author_name,
    b.category_id,
    c.category_name,
    b.visit_count,
    b.favorite_count,
    b.rating,
    (b.visit_count * 0.5 + b.favorite_count * 0.3 + COALESCE(b.rating, 0) * 100 * 0.2) AS popularity_score,
    RANK() OVER (ORDER BY b.visit_count DESC) AS visit_rank,
    RANK() OVER (ORDER BY b.favorite_count DESC) AS favorite_rank
FROM book b
LEFT JOIN category c ON b.category_id = c.id
WHERE b.status = 1 AND b.audit_status = 2;

-- 4. 作者作品统计视图
CREATE OR REPLACE VIEW v_author_stats AS
SELECT 
    b.author_id,
    b.author_name,
    COUNT(DISTINCT b.id) AS total_books,
    SUM(b.total_words) AS total_words,
    SUM(b.visit_count) AS total_visits,
    SUM(b.favorite_count) AS total_favorites,
    AVG(b.rating) AS avg_rating,
    MAX(b.update_time) AS last_update_time
FROM book b
WHERE b.status = 1 AND b.audit_status = 2
GROUP BY b.author_id, b.author_name;

-- 5. 分类书籍统计视图
CREATE OR REPLACE VIEW v_category_stats AS
SELECT 
    c.id AS category_id,
    c.category_name,
    COUNT(DISTINCT b.id) AS book_count,
    SUM(b.total_words) AS total_words,
    SUM(b.visit_count) AS total_visits,
    SUM(b.favorite_count) AS total_favorites,
    AVG(b.rating) AS avg_rating
FROM category c
LEFT JOIN book b ON c.id = b.category_id AND b.status = 1 AND b.audit_status = 2
GROUP BY c.id, c.category_name;

-- 6. 用户书架视图（包含阅读进度）
CREATE OR REPLACE VIEW v_user_bookshelf AS
SELECT 
    bf.id AS favorite_id,
    bf.user_id,
    bf.book_id,
    b.book_name,
    b.author_name,
    b.cover,
    b.category_id,
    c.category_name,
    b.last_chapter_id,
    b.last_chapter_name,
    rh.chapter_id AS current_chapter_id,
    rh.chapter_name AS current_chapter_name,
    bf.create_time AS add_time,
    rh.update_time AS last_read_time,
    CASE 
        WHEN rh.chapter_id IS NOT NULL THEN 
            CONCAT(ROUND((SELECT COUNT(*) FROM chapter ch WHERE ch.book_id = b.id AND ch.id <= rh.chapter_id) * 100.0 / 
            NULLIF((SELECT COUNT(*) FROM chapter ch2 WHERE ch2.book_id = b.id), 0), 2), '%')
        ELSE '0%'
    END AS read_progress
FROM book_favorite bf
JOIN book b ON bf.book_id = b.id
LEFT JOIN category c ON b.category_id = c.id
LEFT JOIN read_history rh ON bf.user_id = rh.user_id AND bf.book_id = rh.book_id
    AND rh.update_time = (SELECT MAX(update_time) FROM read_history rh2 WHERE rh2.user_id = bf.user_id AND rh2.book_id = bf.book_id);

-- 7. 书籍评论统计视图
CREATE OR REPLACE VIEW v_book_comment_stats AS
SELECT 
    b.id AS book_id,
    b.book_name,
    COUNT(c.id) AS total_comments,
    AVG(c.rating) AS avg_comment_rating,
    MAX(c.create_time) AS last_comment_time
FROM book b
LEFT JOIN comment c ON b.id = c.book_id
GROUP BY b.id, b.book_name;

-- 8. 章节内容统计视图
CREATE OR REPLACE VIEW v_chapter_stats AS
SELECT 
    c.id AS chapter_id,
    c.book_id,
    b.book_name,
    c.chapter_num,
    c.chapter_name,
    c.word_count,
    c.status,
    c.audit_status,
    c.create_time,
    c.update_time,
    SUM(c.word_count) OVER (PARTITION BY c.book_id ORDER BY c.chapter_num) AS cumulative_words
FROM chapter c
JOIN book b ON c.book_id = b.id;

-- 9. 每日新增数据统计视图
CREATE OR REPLACE VIEW v_daily_stats AS
SELECT 
    DATE(create_time) AS stat_date,
    COUNT(DISTINCT CASE WHEN create_time >= DATE_SUB(CURDATE(), INTERVAL 1 DAY) THEN id END) AS new_users,
    (SELECT COUNT(*) FROM book WHERE DATE(create_time) = stat_date) AS new_books,
    (SELECT COUNT(*) FROM chapter WHERE DATE(create_time) = stat_date) AS new_chapters,
    (SELECT COUNT(*) FROM comment WHERE DATE(create_time) = stat_date) AS new_comments,
    (SELECT SUM(visit_count) FROM book WHERE DATE(update_time) = stat_date) AS daily_visits
FROM user
GROUP BY DATE(create_time)
ORDER BY stat_date DESC;

-- 10. 待审核内容视图
CREATE OR REPLACE VIEW v_pending_audit AS
SELECT 
    'book' AS content_type,
    b.id AS content_id,
    b.book_name AS content_name,
    b.author_id,
    b.author_name,
    b.audit_status,
    b.submit_time,
    b.audit_remark
FROM book b
WHERE b.audit_status = 1
UNION ALL
SELECT 
    'chapter' AS content_type,
    c.id AS content_id,
    CONCAT(b.book_name, ' - ', c.chapter_name) AS content_name,
    b.author_id,
    b.author_name,
    c.audit_status,
    c.create_time AS submit_time,
    NULL AS audit_remark
FROM chapter c
JOIN book b ON c.book_id = b.id
WHERE c.audit_status = 1
ORDER BY submit_time;
