-- ============================================
-- 数据库触发器创建脚本
-- 用于自动处理数据变更时的相关操作
-- ============================================

-- 设置分隔符（MySQL触发器需要）
DELIMITER //

-- ============================================
-- 1. 书籍表触发器
-- ============================================

-- 触发器：书籍新增时自动更新分类书籍数量
CREATE TRIGGER IF NOT EXISTS trg_book_insert_category
AFTER INSERT ON book
FOR EACH ROW
BEGIN
    -- 更新分类的书籍数量
    UPDATE category 
    SET book_count = book_count + 1,
        update_time = NOW()
    WHERE id = NEW.category_id;
END //

-- 触发器：书籍删除时自动更新分类书籍数量
CREATE TRIGGER IF NOT EXISTS trg_book_delete_category
AFTER DELETE ON book
FOR EACH ROW
BEGIN
    -- 更新分类的书籍数量
    UPDATE category 
    SET book_count = book_count - 1,
        update_time = NOW()
    WHERE id = OLD.category_id;
END //

-- 触发器：书籍分类变更时更新分类书籍数量
CREATE TRIGGER IF NOT EXISTS trg_book_update_category
AFTER UPDATE ON book
FOR EACH ROW
BEGIN
    IF OLD.category_id != NEW.category_id THEN
        -- 减少旧分类的书籍数量
        UPDATE category 
        SET book_count = book_count - 1,
            update_time = NOW()
        WHERE id = OLD.category_id;
        
        -- 增加新分类的书籍数量
        UPDATE category 
        SET book_count = book_count + 1,
            update_time = NOW()
        WHERE id = NEW.category_id;
    END IF;
END //

-- ============================================
-- 2. 章节表触发器
-- ============================================

-- 触发器：章节新增时更新书籍总字数和最后章节信息
CREATE TRIGGER IF NOT EXISTS trg_chapter_insert_book
AFTER INSERT ON chapter
FOR EACH ROW
BEGIN
    DECLARE v_total_words INT;
    DECLARE v_chapter_count INT;
    
    -- 计算书籍总字数
    SELECT COALESCE(SUM(word_count), 0), COUNT(*) 
    INTO v_total_words, v_chapter_count
    FROM chapter 
    WHERE book_id = NEW.book_id AND status = 1;
    
    -- 更新书籍信息
    UPDATE book 
    SET total_words = v_total_words,
        last_chapter_id = NEW.id,
        last_chapter_name = NEW.chapter_name,
        update_time = NOW()
    WHERE id = NEW.book_id;
END //

-- 触发器：章节删除时更新书籍总字数
CREATE TRIGGER IF NOT EXISTS trg_chapter_delete_book
AFTER DELETE ON chapter
FOR EACH ROW
BEGIN
    DECLARE v_total_words INT;
    DECLARE v_last_chapter_id BIGINT;
    DECLARE v_last_chapter_name VARCHAR(255);
    
    -- 计算书籍总字数和最后章节
    SELECT COALESCE(SUM(word_count), 0)
    INTO v_total_words
    FROM chapter 
    WHERE book_id = OLD.book_id AND status = 1;
    
    -- 获取最后章节信息
    SELECT id, chapter_name 
    INTO v_last_chapter_id, v_last_chapter_name
    FROM chapter 
    WHERE book_id = OLD.book_id AND status = 1
    ORDER BY chapter_num DESC 
    LIMIT 1;
    
    -- 更新书籍信息
    UPDATE book 
    SET total_words = v_total_words,
        last_chapter_id = v_last_chapter_id,
        last_chapter_name = v_last_chapter_name,
        update_time = NOW()
    WHERE id = OLD.book_id;
END //

-- 触发器：章节更新时更新书籍总字数
CREATE TRIGGER IF NOT EXISTS trg_chapter_update_book
AFTER UPDATE ON chapter
FOR EACH ROW
BEGIN
    DECLARE v_total_words INT;
    
    IF OLD.word_count != NEW.word_count OR OLD.status != NEW.status THEN
        -- 计算书籍总字数
        SELECT COALESCE(SUM(word_count), 0) 
        INTO v_total_words
        FROM chapter 
        WHERE book_id = NEW.book_id AND status = 1;
        
        -- 更新书籍信息
        UPDATE book 
        SET total_words = v_total_words,
            update_time = NOW()
        WHERE id = NEW.book_id;
    END IF;
END //

-- ============================================
-- 3. 书架表触发器
-- ============================================

-- 触发器：添加书架时更新书籍收藏数
CREATE TRIGGER IF NOT EXISTS trg_favorite_insert_book
AFTER INSERT ON book_favorite
FOR EACH ROW
BEGIN
    UPDATE book 
    SET favorite_count = favorite_count + 1,
        update_time = NOW()
    WHERE id = NEW.book_id;
END //

-- 触发器：删除书架时更新书籍收藏数
CREATE TRIGGER IF NOT EXISTS trg_favorite_delete_book
AFTER DELETE ON book_favorite
FOR EACH ROW
BEGIN
    UPDATE book 
    SET favorite_count = favorite_count - 1,
        update_time = NOW()
    WHERE id = OLD.book_id;
END //

-- ============================================
-- 4. 阅读历史表触发器
-- ============================================

-- 触发器：新增阅读历史时更新书籍访问数
CREATE TRIGGER IF NOT EXISTS trg_read_history_insert
AFTER INSERT ON read_history
FOR EACH ROW
BEGIN
    UPDATE book 
    SET visit_count = visit_count + 1,
        update_time = NOW()
    WHERE id = NEW.book_id;
END //

-- 触发器：更新阅读历史时更新书籍访问数（只有在创建新记录时才增加）
CREATE TRIGGER IF NOT EXISTS trg_read_history_update
AFTER UPDATE ON read_history
FOR EACH ROW
BEGIN
    IF OLD.book_id != NEW.book_id THEN
        -- 减少旧书籍访问数
        UPDATE book 
        SET visit_count = visit_count - 1,
            update_time = NOW()
        WHERE id = OLD.book_id;
        
        -- 增加新书籍访问数
        UPDATE book 
        SET visit_count = visit_count + 1,
            update_time = NOW()
        WHERE id = NEW.book_id;
    END IF;
END //

-- ============================================
-- 5. 评论表触发器
-- ============================================

-- 触发器：新增评论时更新书籍评分
CREATE TRIGGER IF NOT EXISTS trg_comment_insert_book
AFTER INSERT ON comment
FOR EACH ROW
BEGIN
    DECLARE v_avg_rating DECIMAL(3,2);
    
    -- 计算平均评分
    SELECT COALESCE(AVG(rating), 0) 
    INTO v_avg_rating
    FROM comment 
    WHERE book_id = NEW.book_id;
    
    -- 更新书籍评分
    UPDATE book 
    SET rating = v_avg_rating,
        update_time = NOW()
    WHERE id = NEW.book_id;
END //

-- 触发器：删除评论时更新书籍评分
CREATE TRIGGER IF NOT EXISTS trg_comment_delete_book
AFTER DELETE ON comment
FOR EACH ROW
BEGIN
    DECLARE v_avg_rating DECIMAL(3,2);
    
    -- 计算平均评分
    SELECT COALESCE(AVG(rating), 0) 
    INTO v_avg_rating
    FROM comment 
    WHERE book_id = OLD.book_id;
    
    -- 更新书籍评分
    UPDATE book 
    SET rating = v_avg_rating,
        update_time = NOW()
    WHERE id = OLD.book_id;
END //

-- 触发器：更新评论时更新书籍评分
CREATE TRIGGER IF NOT EXISTS trg_comment_update_book
AFTER UPDATE ON comment
FOR EACH ROW
BEGIN
    DECLARE v_avg_rating DECIMAL(3,2);
    
    IF OLD.rating != NEW.rating THEN
        -- 计算平均评分
        SELECT COALESCE(AVG(rating), 0) 
        INTO v_avg_rating
        FROM comment 
        WHERE book_id = NEW.book_id;
        
        -- 更新书籍评分
        UPDATE book 
        SET rating = v_avg_rating,
            update_time = NOW()
        WHERE id = NEW.book_id;
    END IF;
END //

-- ============================================
-- 6. 用户表触发器
-- ============================================

-- 触发器：用户注册时创建默认书架记录
CREATE TRIGGER IF NOT EXISTS trg_user_insert_default
AFTER INSERT ON user
FOR EACH ROW
BEGIN
    -- 可以在这里添加用户注册后的默认操作
    -- 例如：发送欢迎通知、创建默认设置等
    INSERT INTO user_settings (user_id, theme, font_size, created_time)
    VALUES (NEW.id, 'default', 14, NOW());
END //

-- ============================================
-- 7. 作者申请表触发器
-- ============================================

-- 触发器：作者申请通过时更新用户角色
CREATE TRIGGER IF NOT EXISTS trg_author_apply_approved
AFTER UPDATE ON author_apply
FOR EACH ROW
BEGIN
    IF OLD.status != 2 AND NEW.status = 2 THEN
        -- 更新用户角色为作者（role = 2）
        UPDATE user 
        SET role = 2,
            update_time = NOW()
        WHERE id = NEW.user_id;
    END IF;
END //

-- ============================================
-- 8. 数据审计触发器
-- ============================================

-- 触发器：书籍数据变更审计
CREATE TRIGGER IF NOT EXISTS trg_book_audit
AFTER UPDATE ON book
FOR EACH ROW
BEGIN
    -- 记录重要字段的变更
    IF OLD.status != NEW.status OR OLD.audit_status != NEW.audit_status THEN
        INSERT INTO data_audit_log (
            table_name, 
            record_id, 
            operation_type, 
            old_values, 
            new_values, 
            create_time
        ) VALUES (
            'book',
            NEW.id,
            'UPDATE',
            JSON_OBJECT('status', OLD.status, 'audit_status', OLD.audit_status),
            JSON_OBJECT('status', NEW.status, 'audit_status', NEW.audit_status),
            NOW()
        );
    END IF;
END //

-- 恢复分隔符
DELIMITER ;