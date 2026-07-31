ALTER TABLE `message_record`
    ADD COLUMN `subject` varchar(200) DEFAULT NULL COMMENT '消息主题' AFTER `params`,
    ADD COLUMN `business_source` varchar(100) NOT NULL DEFAULT '' COMMENT '业务来源' AFTER `subject`;