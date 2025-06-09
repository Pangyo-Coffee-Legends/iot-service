SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS sensors;
DROP TABLE IF EXISTS sensor_member_mappings;
DROP TABLE IF EXISTS places;

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

CREATE TABLE places (
                        place_no    BIGINT AUTO_INCREMENT PRIMARY KEY,
                        place_name  VARCHAR(100) NOT NULL UNIQUE,   -- 예: '1층'
                        image_path  VARCHAR(255) NULL        -- 1층 평면도 이미지 경로
);
