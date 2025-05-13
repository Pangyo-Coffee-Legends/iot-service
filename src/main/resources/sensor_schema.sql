SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE IF EXISTS sensors;
CREATE TABLE `sensors` (
                           `sensor_no`	BIGINT	NOT NULL,
                           `sensor_name`	VARCHAR(50)	NOT NULL,
                           `sensor_type`	VARCHAR(50)	NOT NULL,
                           `sensor_state`	BOOLEAN	NOT NULL,
                           `location`	VARCHAR(100)	NOT NULL
);