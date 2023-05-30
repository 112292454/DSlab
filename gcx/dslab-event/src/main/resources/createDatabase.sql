create table ds_lab.event
(
    event_id      int auto_increment comment '日程id'
        primary key,
    `name`        varchar(30)          not null comment '日程名' unique,
    event_type    varchar(30)          null comment '日程类型',
    is_online     tinyint(1) default 0 not null comment '日程地点类型, 是否是线上',
    is_group      tinyint(1) default 0 not null comment '日程参与者类型, 是否是集体, 默认集体',
    custom_type   varchar(255)         null comment '活动的类型 个人的包括有: 自习、锻炼、外出等 集体活动包括有：班会、小组作业、创新创业、聚餐等',
    building_id   int                  null comment '日程地点, 线下地点的id',
    building_name varchar(255)         null comment '日程地点, 线下地点的名称',
    link          varchar(255)         null comment '日程地点, 线上链接',
    `date`        timestamp            null comment '日程日期, 日期字符串',
    start_time    timestamp            null comment '日程起止时间, 时间戳',
    end_time      timestamp            null comment '日程终止时间, 时间戳',
    duration      int                  null comment '日程持续事件',
    `cycle`       int        default 0 null comment '日程周期, 默认为0, 不循环',
    `status`      int        default 1 null comment '该日程的状态, 1表示启用, 0表示禁用'
)
    comment '日程表';

create index building_index
    on ds_lab.event (building_id);

create index building_name_index
    on ds_lab.event (building_name);

create index event_index
    on ds_lab.event (event_type);

create index member_index
    on ds_lab.event (is_group);

create index custom_index
    on ds_lab.event (custom_type);

create index name_index
    on ds_lab.event (`name`);

create index pos_index
    on ds_lab.event (is_online);

create index start_index
    on ds_lab.event (start_time);

create table ds_lab.user
(
    user_id    int auto_increment comment '用户id'
        primary key,
    username   varchar(30)             not null comment '用户名',
    mail       varchar(320)            not null comment '邮箱' unique,
    `password` varchar(255)            not null comment '密码',
    type       varchar(30) default '0' null comment '用户类型',
    group_id   int                     not null comment '用户所属组的id'
) auto_increment = 2021210000
    comment '用户表';

create index mail_index
    on ds_lab.user (mail);

create index user_name
    on ds_lab.user (username);

create index type_index
    on ds_lab.user (type);

create index group_index
    on ds_lab.user (group_id);

create table ds_lab.user_event_relation
(
    id       int auto_increment
        primary key,
    group_id int not null comment '用户所属组的id',
    user_id  int not null comment '用户id',
    event_id int not null comment '日程id'
)
    comment '联系表';
create index group_index
    on ds_lab.user_event_relation (group_id);

create index user_index
    on ds_lab.user_event_relation (user_id);

create index event_index
    on ds_lab.user_event_relation (event_id);


# 查询示范
# 查询张某的所有日程
# SELECT
#  u.id, u.name, e.name
# FROM
#  user u
# JOIN
#   user_event_relation uer
# ON
#  user.user_id=u.id
# JOIN
#  event e
# ON
#  scr.event_id=e.id
# WHERE
#  u.name LIKE '张%'