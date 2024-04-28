CREATE TABLE `t_job_instance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL DEFAULT '0',
  `status` smallint(4) NOT NULL DEFAULT '0' COMMENT '执行状态',
  `schedule_type` smallint(4) NOT NULL DEFAULT '0' COMMENT '调度类型',
  `expected_trigger_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '预期触发事件',
  `actually_trigger_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '实际触发事件',
  `scheduler_params`    varchar(512) NOT NULL DEFAULT '' COMMENT '调度参数',
  `scheduler_exception` varchar(256) NOT NULL DEFAULT '' COMMENT '调度异常结果',
  `finish_time` bigint(20) NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status_job` (`status`,`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;



CREATE TABLE `t_job_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_desc` varchar(255) NOT NULL,
  `owner` varchar(64) DEFAULT NULL COMMENT '作者',
  `schedule_type` smallint(4) NOT NULL DEFAULT 0 COMMENT '调度类型',
  `schedule_conf` varchar(2048) DEFAULT NULL COMMENT '调度配置与策略, json 值',
  `open_switch` tinyint(4) NOT NULL DEFAULT '0' COMMENT '任务状态: 0-停止，1-运行',
  `next_trigger_time` bigint(13) NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  `create_time` timestamp DEFAULT CURRENT_TIMESTAMP ,
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`),
  KEY `idx_next_trigger_time` (`next_trigger_time`, `open_switch`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;



CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `role` tinyint(4) NOT NULL COMMENT '角色：0-普通用户、1-管理员',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uname` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;