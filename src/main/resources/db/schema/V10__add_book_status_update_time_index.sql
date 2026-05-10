CREATE INDEX idx_book_status_update_time ON novel_system.book (status, update_time DESC);


SHOW PROCESSLIST;


set global slow_query_log  = ON;

