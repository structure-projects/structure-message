CREATE TABLE `message_accessory` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `message_id` bigint NOT NULL COMMENT '消息ID',
                                      `resource_type` tinyint DEFAULT NULL COMMENT '资源类型：1文件 2道具',
                                      `resource_id` varchar(200) DEFAULT NULL COMMENT '资源ID',
                                      `resource_name` varchar(200) DEFAULT NULL COMMENT '资源名称',
                                      `resource_icon` varchar(500) DEFAULT NULL COMMENT '资源图标',
                                      `resource_code` varchar(100) DEFAULT NULL COMMENT '资源编码',
                                      `resource_desc` varchar(500) DEFAULT NULL COMMENT '资源描述',
                                      `amount` bigint DEFAULT '1' COMMENT '数量',
                                      `state` tinyint DEFAULT '1' COMMENT '状态：1未领取 2已领取',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_message_id` (`message_id`),
                                      KEY `idx_resource_type` (`resource_type`),
                                      KEY `idx_state` (`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息附件表';