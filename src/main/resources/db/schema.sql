create table if not exists `im_user`(
    `id` int not null auto_increment primary key,
    `user_name` varchar(100),
    `user_pwd`  varchar(255)
) comment '用户表';


create table if not exists `im_relation`(
    `id`         int primary key auto_increment comment '关系id',
    `user_id1`   int comment '用户1id',
    `user_id2`   int comment '用户2id',
    `gmt_create` timestamp default CURRENT_TIMESTAMP,
    `gmt_update` timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    unique key `uni_user` (`user_id1`, `user_id2`)
) comment '好友关系表，注意按顺序id1<id2';

create table if not exists `im_msg`(
    `id`           int not null primary key auto_increment comment '主键',
    `content`      varchar(5000) comment '消息内容',
    `to_user_id`   int comment '收件人id',
    `from_user_id` int comment '发件人id',
    `has_read`     int comment '是否阅读'
) comment '消息表';