# 数据库功能扩展说明

本项目已添加完整的数据库高级功能支持，包括索引、视图、序列、触发器、存储过程/函数以及大对象（LOB）存储。

## 📋 功能清单

### ✅ 已添加的功能

| 功能 | 状态 | 说明 |
|------|------|------|
| 数据表 | ✅ 原有 | 基础数据表结构 |
| 用户 | ✅ 原有 | 用户管理功能 |
| **索引** | ✅ **新增** | 为常用查询字段添加索引 |
| **视图** | ✅ **新增** | 创建10个常用查询视图 |
| **序列** | ✅ **新增** | 为各表创建序列生成器 |
| **触发器** | ✅ **新增** | 自动处理数据变更关联 |
| **存储过程** | ✅ **新增** | 封装复杂业务逻辑 |
| **函数** | ✅ **新增** | 常用计算函数 |
| **大对象** | ✅ **新增** | 存储图片、文件等大对象 |

### ❌ 未添加的功能（Oracle特有）

| 功能 | 状态 | 说明 |
|------|------|------|
| 表空间 | ❌ 不支持 | Oracle数据库特有功能 |

---

## 📁 SQL脚本文件

所有SQL脚本位于 `src/main/resources/db/schema/` 目录下：

| 文件名 | 说明 |
|--------|------|
| `V1__add_indexes.sql` | 数据库索引创建脚本 |
| `V2__create_views.sql` | 数据库视图创建脚本 |
| `V3__create_sequences.sql` | 数据库序列创建脚本 |
| `V4__create_triggers.sql` | 数据库触发器创建脚本 |
| `V5__create_procedures_functions.sql` | 存储过程和函数创建脚本 |
| `V6__create_lob_tables.sql` | 大对象表创建脚本 |

---

## 🔍 1. 数据库索引

### 创建的索引

为以下表的常用查询字段创建了索引：

- **user表**: username, email, role, status, create_time
- **book表**: category_id, author_id, status, audit_status, rating, visit_count, favorite_count等
- **chapter表**: book_id, chapter_num, status, audit_status
- **comment表**: book_id, user_id, create_time
- **book_favorite表**: user_id, book_id, create_time
- **read_history表**: user_id, book_id, update_time
- 以及多个**复合索引**用于优化组合查询

---

## 👁️ 2. 数据库视图

### 创建的视图

| 视图名 | 说明 |
|--------|------|
| `v_book_detail` | 书籍详情视图（包含分类名称、章节数、评论数） |
| `v_user_reading_stats` | 用户阅读统计视图 |
| `v_book_popularity` | 书籍热度排行视图 |
| `v_author_stats` | 作者作品统计视图 |
| `v_category_stats` | 分类书籍统计视图 |
| `v_user_bookshelf` | 用户书架视图（包含阅读进度） |
| `v_book_comment_stats` | 书籍评论统计视图 |
| `v_chapter_stats` | 章节内容统计视图 |
| `v_daily_stats` | 每日新增数据统计视图 |
| `v_pending_audit` | 待审核内容视图 |

---

## 🔢 3. 数据库序列

### 创建的序列

为以下表创建了序列生成器：

- `seq_user_id` - 用户ID序列
- `seq_book_id` - 书籍ID序列
- `seq_chapter_id` - 章节ID序列
- `seq_category_id` - 分类ID序列
- `seq_comment_id` - 评论ID序列
- `seq_book_favorite_id` - 书架ID序列
- `seq_read_history_id` - 阅读历史ID序列
- `seq_author_apply_id` - 作者申请ID序列
- `seq_book_rank_id` - 排行榜ID序列
- `seq_admin_id` - 管理员ID序列

### Java代码支持

- `SequenceConfig.java` - 序列配置类
- `SequenceGenerator.java` - 序列生成器工具类

---

## ⚡ 4. 数据库触发器

### 创建的触发器

#### 书籍表触发器
- `trg_book_insert_category` - 新增书籍时更新分类书籍数量
- `trg_book_delete_category` - 删除书籍时更新分类书籍数量
- `trg_book_update_category` - 修改书籍分类时更新分类书籍数量

#### 章节表触发器
- `trg_chapter_insert_book` - 新增章节时更新书籍总字数和最后章节
- `trg_chapter_delete_book` - 删除章节时更新书籍总字数
- `trg_chapter_update_book` - 更新章节时更新书籍总字数

#### 书架表触发器
- `trg_favorite_insert_book` - 添加书架时更新书籍收藏数
- `trg_favorite_delete_book` - 删除书架时更新书籍收藏数

#### 阅读历史表触发器
- `trg_read_history_insert` - 新增阅读历史时更新书籍访问数
- `trg_read_history_update` - 更新阅读历史时更新书籍访问数

#### 评论表触发器
- `trg_comment_insert_book` - 新增评论时更新书籍评分
- `trg_comment_delete_book` - 删除评论时更新书籍评分
- `trg_comment_update_book` - 更新评论时更新书籍评分

#### 作者申请表触发器
- `trg_author_apply_approved` - 作者申请通过时更新用户角色

#### 数据审计触发器
- `trg_book_audit` - 书籍数据变更审计

---

## 📝 5. 存储过程和函数

### 存储过程

| 存储过程名 | 说明 |
|-----------|------|
| `sp_get_book_detail` | 获取书籍详情（包含统计信息） |
| `sp_get_user_reading_stats` | 获取用户阅读统计 |
| `sp_get_book_ranking` | 获取书籍排行榜 |
| `sp_update_book_ranking` | 更新书籍排行榜 |
| `sp_search_books` | 搜索书籍 |
| `sp_clean_expired_read_history` | 清理过期阅读历史 |
| `sp_batch_audit_books` | 批量审核书籍 |

### 函数

| 函数名 | 说明 |
|--------|------|
| `fn_calc_read_progress` | 计算书籍阅读进度百分比 |
| `fn_get_book_word_count` | 获取书籍总字数 |
| `fn_is_book_favorited` | 检查用户是否已收藏书籍 |
| `fn_get_user_reading_minutes` | 获取用户阅读时长（分钟） |
| `fn_format_word_count` | 格式化字数（转换为万字/千字） |
| `fn_calc_book_heat_score` | 计算书籍热度分数 |
| `fn_get_user_level_name` | 获取用户等级名称 |
| `fn_generate_order_no` | 生成唯一订单号 |

---

## 📦 6. 大对象（LOB）支持

### 创建的LOB表

| 表名 | 说明 |
|------|------|
| `book_cover_blob` | 书籍封面图片存储表（LONGBLOB） |
| `user_avatar_blob` | 用户头像图片存储表（LONGBLOB） |
| `chapter_content_lob` | 章节内容存储表（LONGTEXT） |
| `book_description_lob` | 书籍描述存储表（LONGTEXT） |
| `attachment_file` | 通用附件文件存储表（LONGBLOB） |
| `rich_content` | 富文本内容存储表（LONGTEXT） |
| `system_config_lob` | 系统配置存储表（LONGTEXT） |
| `data_backup` | 数据备份存储表（LONGBLOB） |
| `operation_log_lob` | 操作日志存储表（LONGTEXT） |
| `data_audit_log` | 数据审计日志存储表（LONGTEXT） |

### Java代码支持

- `BookCoverBlob.java` - 书籍封面实体类
- `ChapterContentLob.java` - 章节内容实体类
- `AttachmentFile.java` - 附件文件实体类
- `SystemConfigLob.java` - 系统配置实体类
- `LobStorageService.java` - 大对象存储服务接口
- `LobStorageServiceImpl.java` - 大对象存储服务实现

---

## 🚀 使用方法

### 执行SQL脚本

按顺序执行以下SQL脚本：

```sql
-- 1. 创建索引
source V1__add_indexes.sql

-- 2. 创建视图
source V2__create_views.sql

-- 3. 创建序列
source V3__create_sequences.sql

-- 4. 创建触发器
source V4__create_triggers.sql

-- 5. 创建存储过程和函数
source V5__create_procedures_functions.sql

-- 6. 创建LOB表
source V6__create_lob_tables.sql
```

### 使用序列生成器

```java
@Autowired
private SequenceGenerator sequenceGenerator;

// 获取用户ID
Long userId = sequenceGenerator.nextUserId();

// 获取书籍ID
Long bookId = sequenceGenerator.nextBookId();

// 获取章节ID
Long chapterId = sequenceGenerator.nextChapterId();
```

### 使用大对象存储服务

```java
@Autowired
private LobStorageService lobStorageService;

// 保存书籍封面
BookCoverBlob cover = lobStorageService.saveBookCover(bookId, multipartFile);

// 获取封面图片
byte[] coverData = lobStorageService.getBookCoverData(bookId);

// 保存章节内容
ChapterContentLob content = lobStorageService.saveChapterContent(chapterId, contentText);

// 获取章节内容
String content = lobStorageService.getChapterContent(chapterId);

// 上传附件
AttachmentFile attachment = lobStorageService.uploadAttachment(file, "book", bookId, userId);
```

### 调用存储过程

```java
// 使用JdbcTemplate调用存储过程
jdbcTemplate.execute("CALL sp_get_book_detail(?)", (CallableStatementCallback<Void>) cs -> {
    cs.setLong(1, bookId);
    cs.execute();
    return null;
});
```

### 调用函数

```sql
-- 计算阅读进度
SELECT fn_calc_read_progress(1001, 5001);

-- 获取书籍字数
SELECT fn_get_book_word_count(1001);

-- 格式化字数
SELECT fn_format_word_count(15000);

-- 生成订单号
SELECT fn_generate_order_no('ORD');
```

---

## ⚠️ 注意事项

1. **MySQL版本要求**: 需要MySQL 8.0+ 版本以支持序列功能
2. **字符集**: 所有表使用utf8mb4字符集以支持完整的中文和emoji
3. **存储引擎**: 使用InnoDB存储引擎以支持事务
4. **LOB存储**: 大对象数据存储在数据库中，适合中小文件（<50MB），大文件建议使用对象存储服务
5. **触发器**: 触发器会自动维护关联数据，但在批量操作时可能影响性能
6. **索引**: 索引会提高查询性能但会降低写入性能，请根据实际需求调整

---

## 📊 性能优化建议

1. **定期分析表**: `ANALYZE TABLE table_name;`
2. **优化大表**: 对大数据量表进行分区
3. **监控慢查询**: 开启慢查询日志
4. **定期清理**: 清理过期的操作日志和审计日志
5. **备份策略**: 定期备份LOB表，数据量大的表单独备份

---

## 🔧 扩展功能

如需添加更多功能，可以：

1. 在 `V5__create_procedures_functions.sql` 中添加新的存储过程和函数
2. 在 `V6__create_lob_tables.sql` 中添加新的大对象表
3. 在 `LobStorageService` 接口中添加新的操作方法

---

## 📞 技术支持

如有问题，请参考：
- MySQL官方文档: https://dev.mysql.com/doc/
- MyBatis Plus文档: https://baomidou.com/