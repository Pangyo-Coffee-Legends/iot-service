package com.nhnacademy.iot_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sensor_member_mappings")
public class SensorMemberMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_mb_mapping_no")
    private Long mappingNo;

    /**
     * 매핑된 규칙(Rule) 엔티티.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_no", referencedColumnName = "sensor_no", nullable = false)
    private Sensor sensor;

    /**
     * 매핑된 멤버(사용자) 식별자.
     * 실제 멤버 정보는 외부 시스템(API)에서 관리합니다.
     */
    @Column(name = "mb_no", nullable = false)
    private Long mbNo;

    private SensorMemberMapping(Sensor sensor, Long mbNo) {
        this.sensor = sensor;
        this.mbNo = mbNo;
    }

    public static SensorMemberMapping ofNewSensorMemberMapping (Sensor sensor, Long mbNo) {
        return new SensorMemberMapping(sensor, mbNo);
    }
}
