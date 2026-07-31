CREATE TABLE `internal_message` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `org_id` bigint NOT NULL,
                                    `receiver` varchar(100) NOT NULL,
                                    `title` varchar(200) DEFAULT NULL,
                                    `content` text NOT NULL,
                                    `business_id` varchar(100) DEFAULT NULL,
                                    `priority` int DEFAULT '5',
                                    `is_read` tinyint(1) DEFAULT '0',
                                    `create_time` datetime NOT NULL,
                                    `read_time` datetime DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `idx_receiver` (`receiver`),
                                    KEY `idx_org_id` (`org_id`),
                                    KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `channel` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `channel_code` varchar(50) NOT NULL COMMENT '通道编码',
                              `channel_name` varchar(100) NOT NULL COMMENT '通道名称',
                              `channel_type` varchar(20) NOT NULL COMMENT '通道类型：INTERNAL、SMS、EMAIL',
                              `plugin_class` varchar(200) NOT NULL COMMENT '插件实现类',
                              `status` tinyint DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `channel_code` (`channel_code`),
                              KEY `idx_channel_code` (`channel_code`),
                              KEY `idx_channel_type` (`channel_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通道表';

-- ----------------------------
-- Records of channel
-- ----------------------------
BEGIN;
INSERT INTO `channel` (`id`, `channel_code`, `channel_name`, `channel_type`, `plugin_class`, `status`, `create_time`, `update_time`) VALUES (1, 'EMAIL', '企业邮件通道', 'EMAIL', 'com.structure.message.plugin.email.EmailMessagePlugin', 1, NULL, '2026-05-10 19:35:26');
INSERT INTO `channel` (`id`, `channel_code`, `channel_name`, `channel_type`, `plugin_class`, `status`, `create_time`, `update_time`) VALUES (2, 'SMS', '短信通道', 'SMS', 'com.structure.message.plugin.sms.SmsMessagePlugin', 1, NULL, '2026-05-10 21:28:46');
INSERT INTO `channel` (`id`, `channel_code`, `channel_name`, `channel_type`, `plugin_class`, `status`, `create_time`, `update_time`) VALUES (3, 'INTERNAL', '站内消息', 'INTERNAL', 'com.structure.message.plugin.internal.InternalMessagePlugin', 1, '2026-05-10 21:28:45', '2026-05-10 21:28:45');
COMMIT;

-- ----------------------------
-- Table structure for message_record
-- ----------------------------
CREATE TABLE `message_record` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `org_id` bigint NOT NULL COMMENT '组织ID',
                                     `business_id` varchar(100) DEFAULT NULL COMMENT '业务ID',
                                     `template_id` bigint DEFAULT NULL COMMENT '模板ID',
                                     `channel_id` bigint NOT NULL COMMENT '通道ID',
                                     `receiver` varchar(500) NOT NULL COMMENT '接收者',
                                     `content` text COMMENT '消息内容',
                                     `params` json DEFAULT NULL COMMENT '模板参数',
                                     `status` tinyint DEFAULT '0' COMMENT '状态：0-待发送，1-发送中，2-成功，3-失败',
                                     `error_msg` text COMMENT '错误信息',
                                     `send_time` datetime DEFAULT NULL COMMENT '发送时间',
                                     `retry_times` int DEFAULT '0' COMMENT '重试次数',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`),
                                     KEY `idx_org_business` (`org_id`,`business_id`),
                                     KEY `idx_status` (`status`),
                                     KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息记录表';


CREATE TABLE `org_channel_config` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `org_id` bigint NOT NULL COMMENT '组织ID',
                                         `channel_id` bigint NOT NULL COMMENT '通道ID',
                                         `config_value` text COMMENT '配置值',
                                         `status` tinyint DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_org_channel_key` (`org_id`,`channel_id`),
                                         KEY `idx_org_channel` (`org_id`,`channel_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织通道配置表';


BEGIN;
INSERT INTO `org_channel_config` (`id`, `org_id`, `channel_id`, `config_value`, `status`, `create_time`, `update_time`) VALUES (1, 1, 1, '{\"host\":\"smtp.qq.com\",\"port\":\"465\",\"username\":\"yunjida-mail@qq.com\",\"password\":\"ursqzgylxxijchjb\",\"auth\":\"true\",\"starttls\":\"false\",\"ssl\":\"true\",\"connectiontimeout\":\"5000\",\"timeout\":\"5000\",\"writetimeout\":\"10000\",\"from\":\"yunjida-mail@qq.com\"}', 1, '2026-05-10 19:31:17', '2026-05-10 19:32:01');
INSERT INTO `org_channel_config` (`id`, `org_id`, `channel_id`, `config_value`, `status`, `create_time`, `update_time`) VALUES (2, 1, 2, '{\"provider\":\"aliyun\",\"accessKeyId\":\"test-access-key-id\",\"accessKeySecret\":\"test-access-key-secret\",\"signName\":\"测试签名\",\"region\":\"cn-hangzhou\",\"domain\":\"dysmsapi.aliyuncs.com\"}', 1, '2026-05-10 20:44:56', '2026-05-10 22:44:45');
COMMIT;

