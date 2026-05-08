create table novel_system.admin
(
    id          bigint auto_increment comment '管理员ID'
        primary key,
    username    varchar(50)                        not null comment '用户名',
    password    varchar(100)                       not null comment '密码',
    real_name   varchar(50)                        null comment '真实姓名',
    role        tinyint  default 0                 null comment '角色：0-超级管理员 1-普通管理员',
    status      tinyint  default 1                 null comment '状态：0-禁用 1-正常',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_username
        unique (username)
)
    comment '管理员表';

create table novel_system.author_apply
(
    id          bigint auto_increment comment '申请ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    real_name   varchar(50)                        null comment '真实姓名',
    id_card     varchar(18)                        null comment '身份证号',
    phone       varchar(11)                        null comment '联系电话',
    reason      text                               null comment '申请理由',
    status      tinyint  default 0                 null comment '状态：0-待审核 1-已通过 2-已拒绝',
    remark      varchar(255)                       null comment '审核备注',
    create_time datetime default CURRENT_TIMESTAMP null comment '申请时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '审核时间'
)
    comment '作者申请表';

create index idx_user_id
    on novel_system.author_apply (user_id);

create table novel_system.book
(
    id                bigint auto_increment comment '小说ID'
        primary key,
    book_name         varchar(100)                            not null comment '小说名称',
    category_id       bigint                                  not null comment '分类ID',
    author_id         bigint                                  not null comment '作者ID',
    author_name       varchar(50)                             null comment '作者名称',
    cover             varchar(255)                            null comment '封面图片',
    description       text                                    null comment '小说简介',
    publication_date  date                                    null comment '出版日期',
    status            tinyint       default 0                 null comment '状态：0-连载 1-完结 2-草稿',
    visit_count       bigint        default 0                 null comment '点击量',
    favorite_count    bigint        default 0                 null comment '收藏数',
    total_words       int           default 0                 null comment '总字数',
    rating            decimal(2, 1) default 0.0               null comment '评分',
    last_chapter_id   bigint                                  null comment '最新章节ID',
    last_chapter_name varchar(200)                            null comment '最新章节名称',
    create_time       datetime      default CURRENT_TIMESTAMP null,
    update_time       datetime      default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '小说信息表';

create index idx_author_id
    on novel_system.book (author_id);

create index idx_category_id
    on novel_system.book (category_id);

create table novel_system.book_favorite
(
    id                     bigint auto_increment
        primary key,
    user_id                bigint                             not null comment '用户ID',
    book_id                bigint                             not null comment '小说ID',
    create_time            datetime default CURRENT_TIMESTAMP null,
    last_read_chapter_id   bigint                             null comment '最后阅读章节ID',
    last_read_chapter_num  int                                null comment '最后阅读章节序号',
    last_read_chapter_name varchar(255)                       null comment '最后阅读章节名称',
    read_progress          int      default 0                 null comment '阅读进度百分比',
    last_read_time         datetime                           null comment '最后阅读时间',
    type                   tinyint  default 1                 null comment '类型：1-书架 2-收藏',
    constraint uk_user_book_type
        unique (user_id, book_id, type)
)
    comment '小说收藏表';

create table novel_system.book_rank
(
    id          bigint auto_increment
        primary key,
    book_id     bigint                             not null comment '小说ID',
    rank_type   tinyint                            not null comment '排行榜类型：1-点击榜 2-收藏榜 3-评分榜 4-更新榜 5-推荐榜',
    rank_num    int                                not null comment '排名',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_book_rank_type
        unique (book_id, rank_type)
)
    comment '排行榜表';

create index idx_rank_type_num
    on novel_system.book_rank (rank_type, rank_num);

create table novel_system.category
(
    id             bigint auto_increment comment '分类ID'
        primary key,
    name           varchar(50)                        not null comment '分类名称',
    work_direction tinyint                            not null comment '作品方向：0-男频 1-女频',
    sort           int      default 0                 null comment '排序',
    create_time    datetime default CURRENT_TIMESTAMP null
)
    comment '小说分类表';

create table novel_system.chapter
(
    id           bigint auto_increment comment '章节ID'
        primary key,
    book_id      bigint                             not null comment '小说ID',
    chapter_num  int                                not null comment '章节序号',
    chapter_name varchar(200)                       not null comment '章节名称',
    content      longtext                           null comment '章节内容',
    word_count   int      default 0                 null comment '字数',
    status       tinyint  default 0                 null comment '状态：0-草稿 1-已发布',
    create_time  datetime default CURRENT_TIMESTAMP null,
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '小说章节表';

create index idx_book_id
    on novel_system.chapter (book_id);

create table novel_system.comment
(
    id          bigint auto_increment
        primary key,
    user_id     bigint                             not null comment '用户ID',
    book_id     bigint                             not null comment '小说ID',
    content     text                               not null comment '评论内容',
    like_count  int      default 0                 null comment '点赞数',
    status      tinyint  default 1                 null comment '状态：0-删除 1-正常',
    create_time datetime default CURRENT_TIMESTAMP null
)
    comment '用户评论表';

create index idx_book_id
    on novel_system.comment (book_id);

create table novel_system.read_history
(
    id           bigint auto_increment
        primary key,
    user_id      bigint                             not null,
    book_id      bigint                             not null,
    chapter_id   bigint                             not null,
    chapter_num  int                                null comment '章节序号',
    chapter_name varchar(200)                       null comment '章节名称',
    create_time  datetime default CURRENT_TIMESTAMP null
)
    comment '阅读历史表';

create index idx_book_id
    on novel_system.read_history (book_id);

create index idx_user_id
    on novel_system.read_history (user_id);

create table novel_system.user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(50)                        not null comment '用户名',
    password    varchar(100)                       not null comment '密码',
    nickname    varchar(50)                        null comment '昵称',
    email       varchar(100)                       null comment '邮箱',
    avatar      varchar(255)                       null comment '头像URL',
    role        tinyint  default 0                 null comment '角色：0-读者 1-作者 2-管理员',
    status      tinyint  default 1                 null comment '状态：0-禁用 1-正常',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_username
        unique (username)
)
    comment '用户表';

