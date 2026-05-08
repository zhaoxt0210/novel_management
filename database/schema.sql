-- 创建数据库


-- 用户表
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-读者 1-作者 2-管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 作者申请表
CREATE TABLE `author_apply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `phone` VARCHAR(11) COMMENT '联系电话',
    `reason` TEXT COMMENT '申请理由',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审核 1-已通过 2-已拒绝',
    `remark` VARCHAR(255) COMMENT '审核备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '审核时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作者申请表';

-- 管理员表
CREATE TABLE `admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `role` TINYINT DEFAULT 0 COMMENT '角色：0-超级管理员 1-普通管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 小说分类表
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `work_direction` TINYINT NOT NULL COMMENT '作品方向：0-男频 1-女频',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说分类表';

-- 小说信息表
CREATE TABLE `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '小说ID',
    `book_name` VARCHAR(100) NOT NULL COMMENT '小说名称',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `author_id` BIGINT NOT NULL COMMENT '作者ID',
    `author_name` VARCHAR(50) COMMENT '作者名称',
    `cover` VARCHAR(255) COMMENT '封面图片',
    `description` TEXT COMMENT '小说简介',
    `publication_date` DATE COMMENT '出版日期',
    `total_words` INT DEFAULT 0 COMMENT '总字数',
    `rating` DECIMAL(2,1) DEFAULT 0 COMMENT '评分',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-连载 1-完结 2-草稿',
    `visit_count` BIGINT DEFAULT 0 COMMENT '点击量',
    `favorite_count` BIGINT DEFAULT 0 COMMENT '收藏数',
    `last_chapter_id` BIGINT COMMENT '最新章节ID',
    `last_chapter_name` VARCHAR(200) COMMENT '最新章节名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_category_status` (`category_id`, `status`),
    KEY `idx_category_update` (`category_id`, `update_time`),
    KEY `idx_category_visit` (`category_id`, `visit_count`),
    KEY `idx_category_favorite` (`category_id`, `favorite_count`),
    KEY `idx_status_update` (`status`, `update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说信息表';

-- 小说章节表
CREATE TABLE `chapter` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `book_id` BIGINT NOT NULL COMMENT '小说ID',
    `chapter_num` INT NOT NULL COMMENT '章节序号',
    `chapter_name` VARCHAR(200) NOT NULL COMMENT '章节名称',
    `content` LONGTEXT COMMENT '章节内容',
    `word_count` INT DEFAULT 0 COMMENT '字数',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿 1-已发布',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说章节表';

-- 小说收藏表
CREATE TABLE `book_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '小说ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `last_read_chapter_id` BIGINT COMMENT '最后阅读章节ID',
    `last_read_chapter_num` INT COMMENT '最后阅读章节序号',
    `last_read_chapter_name` VARCHAR(200) COMMENT '最后阅读章节名称',
    `read_progress` INT DEFAULT 0 COMMENT '阅读进度(百分比)',
    `last_read_time` DATETIME COMMENT '最后阅读时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_user_last_read` (`user_id`, `last_read_time`),
    KEY `idx_user_progress` (`user_id`, `read_progress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说收藏表';

-- 用户阅读历史表
CREATE TABLE `read_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `book_id` BIGINT NOT NULL,
    `chapter_id` BIGINT NOT NULL,
    `chapter_num` INT COMMENT '章节序号',
    `chapter_name` VARCHAR(200) COMMENT '章节名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅读历史表';

-- 用户评论表
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '小说ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-删除 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户评论表';

-- 排行榜表
CREATE TABLE `book_rank` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `book_id` BIGINT NOT NULL COMMENT '小说ID',
    `rank_type` TINYINT NOT NULL COMMENT '排行榜类型：1-点击榜 2-收藏榜 3-评分榜 4-更新榜 5-推荐榜',
    `rank_num` INT NOT NULL COMMENT '排名',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_book_rank_type` (`book_id`, `rank_type`),
    KEY `idx_rank_type_num` (`rank_type`, `rank_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜表';

-- 插入初始数据
-- 管理员账号：admin / admin123
INSERT INTO `admin` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 0);

-- 读者账号：reader / reader123
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES
('reader', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '热心读者', 0);

-- 作者账号：writer / writer123
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES
('writer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '小说作家', 1);

-- 小说分类
INSERT INTO `category` (`name`, `work_direction`, `sort`) VALUES
('玄幻奇幻', 0, 1),
('武侠仙侠', 0, 2),
('都市言情', 0, 3),
('科幻悬疑', 0, 4),
('历史军事', 0, 5),
('古代言情', 1, 1),
('现代言情', 1, 2),
('穿越重生', 1, 3);

-- 示例小说
INSERT INTO `book` (`book_name`, `category_id`, `author_id`, `author_name`, `description`, `status`) VALUES
('修仙界首富', 2, 3, '小说作家', '一个现代青年穿越到修仙界，不修法术只做生意，成为修仙界首富的故事。', 0),
('都市极品医神', 3, 3, '小说作家', '一代医神回归都市，悬壶济世，谱写传奇人生。', 0);

-- 示例章节
INSERT INTO `chapter` (`book_id`, `chapter_num`, `chapter_name`, `content`, `status`) VALUES
(1, 1, '穿越异界', '夜深人静，李明从梦中惊醒，发现自己身处一个完全陌生的世界。', 1),
(1, 2, '初到仙城', '李明站在仙城门口，看着来来往往的修仙者，心中充满了震撼。', 1),
(2, 1, '医神归来', '飞机缓缓降落，林逸看着窗外的城市夜景，嘴角露出微笑。', 1);



INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`) VALUES
                                                                                       ('reader_01', '$2a$10$7v1X.h8fG5Q9uY2zR6wE5Oq9Z1a2b3c4d5e6f7g8h9i0j1k2l3m4n', '书荒小王子', 'reader01@163.com', 0, 1),
                                                                                       ('reader_02', '$2a$10$9w2Y.i9gH6R0vZ3xS7rF6Pr0A2b3c4d5e6f7g8h9i0j1k2l3m4n5o', '潜水员007', 'reader02@gmail.com', 0, 1),
                                                                                       ('reader_03', '$2a$10$1x3Z.j0hI7S1wA4yT8sG7Qs1B3c4d5e6f7g8h9i0j1k2l3m4n5o6p', '午后阳光', 'reader03@qq.com', 0, 1),
                                                                                       ('reader_04', '$2a$10$2y4A.k1iJ8T2xB5zU9tH8Rt2C4d5e6f7g8h9i0j1k2l3m4n5o6p7q', '追书狂魔', 'reader04@126.com', 0, 1),
                                                                                       ('reader_05', '$2a$10$3z5B.l2jK9U3yC6aV0uI9Su3D5e6f7g8h9i0j1k2l3m4n5o6p7q8r', '吃瓜群众', 'reader05@outlook.com', 0, 1),
                                                                                       ('reader_06', '$2a$10$4a6C.m3kL0V4zD7bW1vJ0Tv4E6f7g8h9i0j1k2l3m4n5o6p7q8r9s', '墨色生香', 'reader06@163.com', 0, 1),
                                                                                       ('reader_07', '$2a$10$5b7D.n4lM1W5aE8cX2wK1Uw5F7g8h9i0j1k2l3m4n5o6p7q8r9s0t', '逻辑严密', 'reader07@gmail.com', 0, 1),
                                                                                       ('reader_08', '$2a$10$6c8E.o5mN2X6bF9dY3xL2Vx6G8h9i0j1k2l3m4n5o6p7q8r9s0t1u', '老书虫一枚', 'reader08@qq.com', 0, 1),
                                                                                       ('reader_09', '$2a$10$7d9F.p6nO3Y7cG0eZ4yM3Wy7H9i0j1k2l3m4n5o6p7q8r9s0t1u2v', '梦里看花', 'reader09@126.com', 0, 1),
                                                                                       ('reader_10', '$2a$10$8e0G.q7oP4Z8dH1fa5zN4Xz8I0j1k2l3m4n5o6p7q8r9s0t1u2v3w', '键盘侠在此', 'reader10@outlook.com', 0, 1),
                                                                                       ('reader_11', '$2a$10$9f1H.r8pQ5a9eI2gb6aO5Y0J1k2l3m4n5o6p7q8r9s0t1u2v3w4x', '月下独酌', 'reader11@163.com', 0, 1),
                                                                                       ('reader_12', '$2a$10$0g2I.s9qR6b0fJ3hc7bP6Z1K2l3m4n5o6p7q8r9s0t1u2v3w4x5y', '催更小能手', 'reader12@gmail.com', 0, 1),
                                                                                       ('reader_13', '$2a$10$1h3J.t0rS7c1gK4id8cQ7A2L3m4n5o6p7q8r9s0t1u2v3w4x5y6z', '星辰大海', 'reader13@qq.com', 0, 1),
                                                                                       ('reader_14', '$2a$10$2i4K.u1sT8d2hL5je9dR8B3M4n5o6p7q8r9s0t1u2v3w4x5y6z1a', '一叶知秋', 'reader14@126.com', 0, 1),
                                                                                       ('reader_15', '$2a$10$3j5L.v2tU9e3iM6kf0eS9C4N5o6p7q8r9s0t1u2v3w4x5y6z1a2b', '萌新驾到', 'reader15@outlook.com', 0, 1),
                                                                                       ('reader_16', '$2a$10$4k6M.w3uV0f4jN7lg1fT0D5O6p7q8r9s0t1u2v3w4x5y6z1a2b3c', '淡定自若', 'reader16@163.com', 0, 1),
                                                                                       ('reader_17', '$2a$10$5l7N.x4vW1g5kO8mh2gU1E6P7q8r9s0t1u2v3w4x5y6z1a2b3c4d', '无名氏A', 'reader17@gmail.com', 0, 1),
                                                                                       ('reader_18', '$2a$10$6m8O.y5wX2h6lP9ni3hV2F7Q8r9s0t1u2v3w4x5y6z1a2b3c4d5e', '文学少女', 'reader18@qq.com', 0, 1),
                                                                                       ('reader_19', '$2a$10$7n9P.z6xY3i7mQ0oj4iW3G8R9s0t1u2v3w4x5y6z1a2b3c4d5e6f', '极速蜗牛', 'reader19@126.com', 0, 1),
                                                                                       ('reader_20', '$2a$10$8o0Q.a7yZ4j8nR1pk5jX4H9S0t1u2v3w4x5y6z1a2b3c4d5e6f7g', '读万卷书', 'reader20@outlook.com', 0, 1);




INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`) VALUES
                                                                                       ('author_tang', '$2a$10$X1y2Z3a4B5c6D7e8F9g0h1i2j3k4l5m6n7o8p9q0r1s2t3u4v5w6x', '唐家三少爷', 'tang@write.com', 1, 1),
                                                                                       ('author_tian', '$2a$10$Y2z3A4b5C6d7E8f9G0h1i2j3k4l5m6n7o8p9q0r1s2t3u4v5w6x7y', '天蚕土豆皮', 'tian@write.com', 1, 1),
                                                                                       ('author_mao', '$2a$10$Z3a4B5c6D7e8F9g0h1i2j3k4l5m6n7o8p9q0r1s2t3u4v5w6x7y8z', '猫腻腻', 'mao@write.com', 1, 1),
                                                                                       ('author_feng', '$2a$10$A4b5C6d7E8f9G0h1i2j3k4l5m6n7o8p9q0r1s2t3u4v5w6x7y8z9a', '烽火戏诸侯王', 'feng@write.com', 1, 1),
                                                                                       ('author_liu', '$2a$10$B5c6D7e8F9g0h1i2j3k4l5m6n7o8p9q0r1s2t3u4v5w6x7y8z9a0b', '流浪的蛤蟆功', 'liu@write.com', 1, 1);



INSERT INTO `book` (`book_name`, `category_id`, `author_id`, `author_name`, `description`, `status`, `visit_count`, `favorite_count`, `total_words`) VALUES
                                                                                                                                                         ('剑破星辰', 1, 21, '唐家三少爷', '少年持剑，自荒原起，一剑划破万古星空。', 0, 1250, 180, 450000),
                                                                                                                                                         ('炼丹师的自我修养', 2, 22, '天蚕土豆皮', '谁说炼丹师战斗力弱？看我一炉丹药炸毁城池。', 0, 3200, 450, 1200000),
                                                                                                                                                         ('回到古代当首富', 8, 23, '猫腻腻', '带着现代超市回到古代，这生意没法做了。', 1, 8900, 1200, 2100000),
                                                                                                                                                         ('我有一座冒险屋', 4, 24, '烽火戏诸侯王', '继承了父亲留下的冒险屋后，世界变样了。', 0, 5600, 980, 850000),
                                                                                                                                                         ('大明锦衣卫', 5, 25, '流浪的蛤蟆功', '绣春刀响，且看锦衣卫如何搅动风云。', 1, 4500, 670, 1500000),
                                                                                                                                                         ('代码修仙指南', 1, 21, '唐家三少爷', '当修仙遇上Java，万物皆可对象。', 0, 12000, 3500, 900000),
                                                                                                                                                         ('极品奶爸在都市', 7, 22, '天蚕土豆皮', '仙帝归来，第一件事竟然是给女儿开家长会？', 0, 780, 120, 300000),
                                                                                                                                                         ('我在异界刷短视频', 3, 23, '猫腻腻', '异界土著看呆了：这个叫“奥利给”的男人是谁？', 0, 4300, 800, 500000),
                                                                                                                                                         ('雪落长亭', 6, 24, '烽火戏诸侯王', '宫廷深处的爱恨情仇，如雪般寂静。', 1, 2100, 340, 750000),
                                                                                                                                                         ('机械飞升', 4, 25, '流浪的蛤蟆功', '肉体苦弱，机械飞升，赛博朋克下的修仙。', 0, 3300, 560, 1100000),
                                                                                                                                                         ('斗罗之后传', 1, 21, '唐家三少爷', '全新的武魂觉醒，少年再次踏上征程。', 0, 9500, 2100, 1800000),
                                                                                                                                                         ('异火传说', 2, 22, '天蚕土豆皮', '搜集天下奇火，成就至尊丹帝。', 1, 15000, 4000, 3200000),
                                                                                                                                                         ('择天改命', 1, 23, '猫腻腻', '少年持婚书入京，要与天斗，与命搏。', 1, 6700, 1100, 2500000),
                                                                                                                                                         ('剑来续章', 2, 24, '烽火戏诸侯王', '草鞋少年走出小镇，肩上挑着整座江湖。', 0, 18000, 5000, 4000000),
                                                                                                                                                         ('西游补完计划', 1, 25, '流浪的蛤蟆功', '如果西游是一场实验，谁才是观察者？', 0, 1400, 210, 600000),
                                                                                                                                                         ('我在淘宝买仙丹', 3, 21, '唐家三少爷', '9块9钱包邮的洗髓丹，居然真的有效？', 0, 2200, 430, 400000),
                                                                                                                                                         ('女总裁的贴身保安', 3, 22, '天蚕土豆皮', '虽然是保安，但我其实是兵王。', 1, 3100, 560, 1200000),
                                                                                                                                                         ('重生之我是大佬', 8, 23, '猫腻腻', '重回十八岁，这一世我要拿回属于我的一切。', 0, 5400, 890, 950000),
                                                                                                                                                         ('万妖之主', 1, 24, '烽火戏诸侯王', '人亦有妖心，妖亦有人情。', 1, 1100, 150, 800000),
                                                                                                                                                         ('星际种田日记', 4, 25, '流浪的蛤蟆功', '在荒芜星球种出第一颗大白菜。', 0, 670, 90, 200000);





-- 为book表添加新字段
ALTER TABLE book
    ADD COLUMN publication_date DATE COMMENT '出版日期' AFTER description,
    ADD COLUMN rating DECIMAL(2,1) DEFAULT 0 COMMENT '评分' AFTER total_words;


-- 创建排行榜表
CREATE TABLE `book_rank` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `book_id` BIGINT NOT NULL COMMENT '小说ID',
                             `rank_type` TINYINT NOT NULL COMMENT '排行榜类型：1-点击榜 2-收藏榜 3-评分榜 4-更新榜 5-推荐榜',
                             `rank_num` INT NOT NULL COMMENT '排名',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_book_rank_type` (`book_id`, `rank_type`),
                             KEY `idx_rank_type_num` (`rank_type`, `rank_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排行榜表';

-- 排行榜初始数据
-- 点击榜 (rank_type = 1) - 按visit_count排序
INSERT INTO `book_rank` (`book_id`, `rank_type`, `rank_num`) VALUES
(14, 1, 1), (13, 1, 2), (12, 1, 3), (11, 1, 4), (4, 1, 5), (15, 1, 6), (10, 1, 7), (9, 1, 8), (16, 1, 9), (5, 1, 10);

-- 收藏榜 (rank_type = 2) - 按favorite_count排序
INSERT INTO `book_rank` (`book_id`, `rank_type`, `rank_num`) VALUES
(14, 2, 1), (13, 2, 2), (12, 2, 3), (11, 2, 4), (4, 2, 5), (15, 2, 6), (10, 2, 7), (9, 2, 8), (16, 2, 9), (5, 2, 10);

-- 评分榜 (rank_type = 3) - 按rating排序
INSERT INTO `book_rank` (`book_id`, `rank_type`, `rank_num`) VALUES
(14, 3, 1), (13, 3, 2), (12, 3, 3), (11, 3, 4), (4, 3, 5), (15, 3, 6), (10, 3, 7), (9, 3, 8), (16, 3, 9), (5, 3, 10);

-- 更新榜 (rank_type = 4) - 按update_time排序
INSERT INTO `book_rank` (`book_id`, `rank_type`, `rank_num`) VALUES
(14, 4, 1), (13, 4, 2), (12, 4, 3), (11, 4, 4), (4, 4, 5), (15, 4, 6), (10, 4, 7), (9, 4, 8), (16, 4, 9), (5, 4, 10);

-- 推荐榜 (rank_type = 5) - 综合推荐
INSERT INTO `book_rank` (`book_id`, `rank_type`, `rank_num`) VALUES
(14, 5, 1), (13, 5, 2), (15, 5, 3), (4, 5, 4), (12, 5, 5), (11, 5, 6), (16, 5, 7), (9, 5, 8), (10, 5, 9), (5, 5, 10);



-- 为 book_favorite 表添加缺失的字段
ALTER TABLE `book_favorite`
ADD COLUMN `last_read_chapter_id` BIGINT COMMENT '最后阅读章节ID',
ADD COLUMN `last_read_chapter_num` INT COMMENT '最后阅读章节序号',
ADD COLUMN `last_read_chapter_name` VARCHAR(200) COMMENT '最后阅读章节名称',
ADD COLUMN `read_progress` INT DEFAULT 0 COMMENT '阅读进度(百分比)',
ADD COLUMN `last_read_time` DATETIME COMMENT '最后阅读时间';
