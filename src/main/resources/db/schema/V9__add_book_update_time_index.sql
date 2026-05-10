CREATE INDEX idx_book_audit_status_update_time ON novel_system.book (audit_status, update_time DESC);

CREATE INDEX idx_book_update_time ON novel_system.book (update_time DESC);