CREATE TABLE `id_segment` (
	`day` int NOT NULL COMMENT '日期',
	`version` bigint NOT NULL COMMENT '版本号',
	`val` mediumtext CHARACTER SET utf8mb4 NOT NULL COMMENT 'ID聚合值',
	`idx` int NOT NULL DEFAULT '0' COMMENT 'ID当前索引值',
	PRIMARY KEY (`day`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = 'ID分段';