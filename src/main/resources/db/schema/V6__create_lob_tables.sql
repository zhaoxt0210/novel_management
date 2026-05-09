-- ============================================
-- 大对象（LOB）支持脚本
-- 创建用于存储大对象数据的表
-- ============================================

-- ============================================
-- 1. 书籍封面图片表（存储二进制图片数据）
-- ============================================
CREATE TABLE IF NOT EXISTS book_cover_blob (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL UNIQUE,
    cover_data LONGBLOB NOT NULL COMMENT '封面图片二进制数据',
    cover_type VARCHAR(20) NOT NULL DEFAULT 'image/jpeg' COMMENT '图片类型：image/jpeg, image/png, image/webp',
    file_size INT NOT NULL COMMENT '文件大小（字节）',
    width INT COMMENT '图片宽度',
    height INT COMMENT '图片高度',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍封面图片大对象存储表';

-- ============================================
-- 2. 用户头像图片表
-- ============================================
CREATE TABLE IF NOT EXISTS user_avatar_blob (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    avatar_data LONGBLOB NOT NULL COMMENT '头像图片二进制数据',
    avatar_type VARCHAR(20) NOT NULL DEFAULT 'image/jpeg' COMMENT '图片类型',
    file_size INT NOT NULL COMMENT '文件大小（字节）',
    width INT COMMENT '图片宽度',
    height INT COMMENT '图片高度',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户头像图片大对象存储表';

-- ============================================
-- 3. 章节内容大文本表（分离大文本内容）
-- ============================================
CREATE TABLE IF NOT EXISTS chapter_content_lob (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chapter_id BIGINT NOT NULL UNIQUE,
    content LONGTEXT NOT NULL COMMENT '章节内容（大文本）',
    content_length INT NOT NULL COMMENT '内容长度（字符数）',
    word_count INT NOT NULL COMMENT '字数统计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_chapter_id (chapter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节内容大对象存储表';

-- ============================================
-- 4. 书籍描述大文本表
-- ============================================
CREATE TABLE IF NOT EXISTS book_description_lob (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL UNIQUE,
    description LONGTEXT COMMENT '书籍详细描述',
    description_length INT COMMENT '描述长度',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍描述大对象存储表';

-- ============================================
-- 5. 附件文件表（通用文件存储）
-- ============================================
CREATE TABLE IF NOT EXISTS attachment_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_type VARCHAR(100) NOT NULL COMMENT '文件MIME类型',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_data LONGBLOB NOT NULL COMMENT '文件二进制数据',
    file_hash VARCHAR(64) NOT NULL COMMENT '文件MD5哈希值（用于去重）',
    ref_type VARCHAR(50) COMMENT '关联类型：book/chapter/user等',
    ref_id BIGINT COMMENT '关联ID',
    upload_user_id BIGINT COMMENT '上传用户ID',
    storage_path VARCHAR(500) COMMENT '存储路径（如果使用文件系统存储）',
    storage_type TINYINT DEFAULT 1 COMMENT '存储类型：1-数据库存储，2-文件系统存储',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ref (ref_type, ref_id),
    INDEX idx_file_hash (file_hash),
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件文件大对象存储表';

-- ============================================
-- 6. 富文本内容表（用于公告、帮助文档等）
-- ============================================
CREATE TABLE IF NOT EXISTS rich_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_type VARCHAR(50) NOT NULL COMMENT '内容类型：announcement/help/about等',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content LONGTEXT NOT NULL COMMENT '富文本内容（HTML）',
    content_length INT COMMENT '内容长度',
    author_id BIGINT COMMENT '作者ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_content_type (content_type),
    INDEX idx_status (status),
    INDEX idx_publish_time (publish_time),
    INDEX idx_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='富文本内容大对象存储表';

-- ============================================
-- 7. 系统配置大文本表（存储JSON等配置）
-- ============================================
CREATE TABLE IF NOT EXISTS system_config_lob (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_name VARCHAR(255) COMMENT '配置名称',
    config_value LONGTEXT COMMENT '配置值（大文本/JSON）',
    config_type VARCHAR(20) DEFAULT 'text' COMMENT '配置类型：text/json/xml/html',
    description VARCHAR(500) COMMENT '配置说明',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_is_system (is_system)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置大对象存储表';

-- ============================================
-- 8. 数据备份表（存储大数据量备份）
-- ============================================
CREATE TABLE IF NOT EXISTS data_backup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    backup_name VARCHAR(255) NOT NULL COMMENT '备份名称',
    backup_type VARCHAR(50) NOT NULL COMMENT '备份类型：full/incremental/table',
    backup_data LONGBLOB COMMENT '备份数据（压缩后的二进制）',
    backup_size BIGINT COMMENT '备份大小（字节）',
    table_names TEXT COMMENT '备份的表名（逗号分隔）',
    record_count INT COMMENT '备份记录数',
    start_time DATETIME COMMENT '备份开始时间',
    end_time DATETIME COMMENT '备份结束时间',
    status TINYINT DEFAULT 0 COMMENT '状态：0-备份中，1-完成，2-失败',
    error_message TEXT COMMENT '错误信息',
    create_user_id BIGINT COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_backup_type (backup_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_create_user (create_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据备份大对象存储表';

-- ============================================
-- 9. 操作日志大文本表（存储详细日志）
-- ============================================
CREATE TABLE IF NOT EXISTS operation_log_lob (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    log_type VARCHAR(50) NOT NULL COMMENT '日志类型',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    user_id BIGINT COMMENT '操作用户ID',
    user_name VARCHAR(100) COMMENT '操作用户名',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理信息',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_params LONGTEXT COMMENT '请求参数（JSON）',
    request_body LONGTEXT COMMENT '请求体',
    response_data LONGTEXT COMMENT '响应数据',
    execute_time INT COMMENT '执行时间（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_log_type (log_type),
    INDEX idx_operation_type (operation_type),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志大对象存储表';

-- ============================================
-- 10. 数据审计日志表（存储数据变更记录）
-- ============================================
CREATE TABLE IF NOT EXISTS data_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL COMMENT '表名',
    record_id BIGINT NOT NULL COMMENT '记录ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：INSERT/UPDATE/DELETE',
    old_values LONGTEXT COMMENT '旧值（JSON格式）',
    new_values LONGTEXT COMMENT '新值（JSON格式）',
    change_fields TEXT COMMENT '变更字段（逗号分隔）',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(100) COMMENT '操作人名称',
    operation_ip VARCHAR(50) COMMENT '操作IP',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_table_record (table_name, record_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operator (operator_id),
    INDEX idx_operation_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据审计日志大对象存储表';

-- ============================================
-- 插入默认系统配置
-- ============================================
INSERT INTO system_config_lob (config_key, config_name, config_value, config_type, description, is_system) VALUES
('site.name', '网站名称', '小说管理系统', 'text', '网站显示名称', 1),
('site.logo', '网站Logo', '', 'text', '网站Logo URL', 1),
('site.copyright', '版权信息', '© 2024 小说管理系统 版权所有', 'text', '网站底部版权信息', 1),
('site.icp', 'ICP备案号', '', 'text', '网站ICP备案号', 1),
('upload.maxSize', '上传文件大小限制', '10485760', 'text', '最大上传文件大小（字节），默认10MB', 1),
('upload.allowedTypes', '允许上传的文件类型', 'jpg,jpeg,png,gif,webp,pdf,txt,doc,docx', 'text', '允许上传的文件扩展名', 1),
('book.maxChapterSize', '章节最大字数', '50000', 'text', '单个章节最大字数限制', 1),
('book.autoPublish', '自动发布', 'false', 'text', '章节审核通过后是否自动发布', 1),
('rank.updateInterval', '排行榜更新间隔', '3600', 'text', '排行榜更新间隔（秒），默认1小时', 1),
('security.maxLoginFail', '最大登录失败次数', '5', 'text', '允许的最大登录失败次数', 1),
('security.lockDuration', '账户锁定时间', '1800', 'text', '账户锁定时间（秒），默认30分钟', 1)
ON DUPLICATE KEY UPDATE 
    config_name = VALUES(config_name),
    description = VALUES(description);

-- ============================================
-- 插入默认富文本内容
-- ============================================
INSERT INTO rich_content (content_type, title, content, status, sort_order) VALUES
('about', '关于我们', '<h2>关于小说管理系统</h2><p>这是一个功能完善的小说管理系统，为读者提供优质的小说阅读体验，为作者提供便捷的创作平台。</p>', 1, 1),
('help', '使用帮助', '<h2>如何使用本系统</h2><p>1. 注册账号并登录</p><p>2. 浏览小说或搜索感兴趣的内容</p><p>3. 将喜欢的小说加入书架</p><p>4. 申请成为作者开始创作</p>', 1, 2),
('contact', '联系我们', '<h2>联系方式</h2><p>如有问题或建议，请联系我们。</p>', 1, 3)
ON DUPLICATE KEY UPDATE 
    title = VALUES(title),
    content = VALUES(content);
