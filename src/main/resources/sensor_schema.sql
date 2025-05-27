SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS sensors;
DROP TABLE IF EXISTS sensor_member_mappings;

CREATE TABLE `sensors` (
           `sensor_no`	BIGINT	PRIMARY KEY	AUTO_INCREMENT,
           `sensor_name`	VARCHAR(50)	NOT NULL,
           `sensor_type`	VARCHAR(50)	NOT NULL,
           `sensor_state`	BOOLEAN	NOT NULL,
           `location`	VARCHAR(100)	NOT NULL
);

CREATE TABLE sensor_member_mappings (
            `sensor_mb_mapping_no` BIGINT PRIMARY KEY AUTO_INCREMENT,
            `sensor_no` BIGINT NOT NULL,
            `mb_no` BIGINT NOT NULL
);