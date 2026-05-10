CREATE INDEX idx_book_audit_status ON novel_system.book (audit_status);

CREATE INDEX idx_book_audit_status_submit_time ON novel_system.book (audit_status, submit_time DESC);

CREATE INDEX idx_book_author_id_audit_status ON novel_system.book (author_id, audit_status);

CREATE INDEX idx_chapter_book_id_audit_status ON novel_system.chapter (book_id, audit_status);

CREATE INDEX idx_author_apply_status_create_time ON novel_system.author_apply (status, create_time DESC);