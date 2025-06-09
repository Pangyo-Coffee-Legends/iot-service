INSERT INTO sensors (sensor_no, sensor_name, sensor_type, sensor_state, location)
VALUES
    (1, '회의실 에어컨', 'aircon', TRUE, '회의실'),
    (2, '회의실 히터', 'heater', FALSE, '회의실'),
    (3, '회의실 환풍기', 'ventilator', TRUE, '회의실'),
    (4, '사무실 에어컨', 'aircon', TRUE, '사무실'),
    (5, '사무실 히터', 'heater', FALSE, '사무실'),
    (6, '사무실 환풍기', 'ventilator', TRUE, '사무실');

INSERT INTO sensor_member_mappings (sensor_no, mb_no)
VALUES
    (1, 96),
    (2, 96),
    (3, 96),
    (4, 98),
    (5, 98),
    (6, 98);

INSERT INTO places (place_name, image_path) VALUES
    ('1층', '/images/floor1.png'),
    ('회의실A', '/images/meeting_a.png'),
    ('회의실B', '/images/meeting_b.png'),
    ('사무실A', '/images/office_a.png'),
    ('사무실B', '/images/office_b.png'),
    ('2층', '/images/floor2.png'),
    ('3층', '/images/floor3.png');