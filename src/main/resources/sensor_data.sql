INSERT INTO sensors (sensor_no, role_no, sensor_name, sensor_type, sensor_state, location)
VALUES
    (1, 101, '회의실 에어컨', 'aircon', TRUE, '회의실'),
    (2, 101, '회의실 히터', 'heater', FALSE, '회의실'),
    (3, 101, '회의실 환풍기', 'ventilator', TRUE, '회의실'),
    (4, 101, '사무실 에어컨', 'aircon', TRUE, '사무실'),
    (5, 101, '사무실 히터', 'heater', FALSE, '사무실'),
    (6, 101, '사무실 환풍기', 'ventilator', TRUE, '사무실'),
    (7, 101, '복도 조명', 'light', TRUE, '복도'),
    (8, 101, '복도 센서', 'motion_sensor', FALSE, '복도'),
    (9, 101, '주방 에어컨', 'aircon', TRUE, '주방'),
    (10, 101, '주방 히터', 'heater', FALSE, '주방');
