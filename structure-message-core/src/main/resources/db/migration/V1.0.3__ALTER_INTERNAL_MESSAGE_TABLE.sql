ALTER TABLE `internal_message`
    ADD COLUMN `type` tinyint DEFAULT '1' COMMENT '消息类型：1站内通知，2附件通知' AFTER `id`,
    ADD COLUMN `sender` varchar(100) DEFAULT NULL COMMENT '发送方' AFTER `type`,
    ADD COLUMN `subject` varchar(200) DEFAULT NULL COMMENT '主题' AFTER `receiver`,
    ADD COLUMN `channel` varchar(50) DEFAULT '1' COMMENT '消息渠道：1内部通知，2短信通知，3邮件通知' AFTER `content`,
    ADD COLUMN `state` tinyint DEFAULT '1' COMMENT '状态：1发送，2已读，3完成' AFTER `channel`,
    ADD COLUMN `create_by` bigint DEFAULT NULL COMMENT '创建人' AFTER `create_time`,
    ADD COLUMN `update_by` bigint DEFAULT NULL COMMENT '修改人' AFTER `read_time`,
    ADD COLUMN `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0-否 1-是' AFTER `update_by`,
    ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间' AFTER `is_deleted`;