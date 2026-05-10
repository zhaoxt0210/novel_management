CREATE INDEX idx_book_category_audit_update_time ON novel_system.book (category_id, audit_status, update_time DESC);

CREATE INDEX idx_book_category_audit_visit_count ON novel_system.book (category_id, audit_status, visit_count DESC);

CREATE INDEX idx_book_category_audit_favorite_count ON novel_system.book (category_id, audit_status, favorite_count DESC);