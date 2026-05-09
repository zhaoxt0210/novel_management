-- ============================================
-- 数据库索引创建脚本
-- 为常用查询字段添加索引以提高查询性能
-- ============================================

-- 1. 用户表索引
CREATE INDEX IF NOT EXISTS idx_user_username ON user(username);
CREATE INDEX IF NOT EXISTS idx_user_email ON user(email);
CREATE INDEX IF NOT EXISTS idx_user_role ON user(role);
CREATE INDEX IF NOT EXISTS idx_user_status ON user(status);
CREATE INDEX IF NOT EXISTS idx_user_create_time ON user(create_time);

-- 2. 书籍表索引
CREATE INDEX IF NOT EXISTS idx_book_category_id ON book(category_id);
CREATE INDEX IF NOT EXISTS idx_book_author_id ON book(author_id);
CREATE INDEX IF NOT EXISTS idx_book_status ON book(status);
CREATE INDEX IF NOT EXISTS idx_book_audit_status ON book(audit_status);
CREATE INDEX IF NOT EXISTS idx_book_rating ON book(rating);
CREATE INDEX IF NOT EXISTS idx_book_visit_count ON book(visit_count);
CREATE INDEX IF NOT EXISTS idx_book_favorite_count ON book(favorite_count);
CREATE INDEX IF NOT EXISTS idx_book_publication_date ON book(publication_date);
CREATE INDEX IF NOT EXISTS idx_book_book_name ON book(book_name);

-- 3. 章节表索引
CREATE INDEX IF NOT EXISTS idx_chapter_book_id ON chapter(book_id);
CREATE INDEX IF NOT EXISTS idx_chapter_chapter_num ON chapter(chapter_num);
CREATE INDEX IF NOT EXISTS idx_chapter_status ON chapter(status);
CREATE INDEX IF NOT EXISTS idx_chapter_audit_status ON chapter(audit_status);

-- 4. 分类表索引
CREATE INDEX IF NOT EXISTS idx_category_status ON category(status);

-- 5. 评论表索引
CREATE INDEX IF NOT EXISTS idx_comment_book_id ON comment(book_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment(user_id);
CREATE INDEX IF NOT EXISTS idx_comment_create_time ON comment(create_time);

-- 6. 书架表索引
CREATE INDEX IF NOT EXISTS idx_book_favorite_user_id ON book_favorite(user_id);
CREATE INDEX IF NOT EXISTS idx_book_favorite_book_id ON book_favorite(book_id);
CREATE INDEX IF NOT EXISTS idx_book_favorite_create_time ON book_favorite(create_time);

-- 7. 阅读历史表索引
CREATE INDEX IF NOT EXISTS idx_read_history_user_id ON read_history(user_id);
CREATE INDEX IF NOT EXISTS idx_read_history_book_id ON read_history(book_id);
CREATE INDEX IF NOT EXISTS idx_read_history_update_time ON read_history(update_time);

-- 8. 作者申请表索引
CREATE INDEX IF NOT EXISTS idx_author_apply_user_id ON author_apply(user_id);
CREATE INDEX IF NOT EXISTS idx_author_apply_status ON author_apply(status);
CREATE INDEX IF NOT EXISTS idx_author_apply_apply_time ON author_apply(apply_time);

-- 9. 排行榜表索引
CREATE INDEX IF NOT EXISTS idx_book_rank_book_id ON book_rank(book_id);
CREATE INDEX IF NOT EXISTS idx_book_rank_rank_type ON book_rank(rank_type);
CREATE INDEX IF NOT EXISTS idx_book_rank_period ON book_rank(period);

-- 10. 管理员表索引
CREATE INDEX IF NOT EXISTS idx_admin_username ON admin(username);
CREATE INDEX IF NOT EXISTS idx_admin_status ON admin(status);

-- 11. 复合索引（用于常用组合查询）
CREATE INDEX IF NOT EXISTS idx_book_category_status ON book(category_id, status);
CREATE INDEX IF NOT EXISTS idx_book_author_status ON book(author_id, status);
CREATE INDEX IF NOT EXISTS idx_chapter_book_num ON chapter(book_id, chapter_num);
CREATE INDEX IF NOT EXISTS idx_read_history_user_book ON read_history(user_id, book_id);
CREATE INDEX IF NOT EXISTS idx_book_favorite_user_book ON book_favorite(user_id, book_id);
