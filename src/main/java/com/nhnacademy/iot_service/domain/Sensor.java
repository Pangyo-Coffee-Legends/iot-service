package com.nhnacademy.iot_service.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Comment;

/**
 * 센서 정보를 담는 JPA 엔티티 클래스입니다.
 * 센서 번호, 이름, 종류, 상태, 설치 장소 정보를 포함합니다.
 */
@Entity
@Getter
@Table(name = "sensors")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_no", nullable = false)
    @Comment("센서 번호")
    private Long sensorNo;

    @Column(nullable = false, length = 50)
    @Comment("센서 이름")
    private String sensorName;

    /**
     * 냉방기: aircon
     * 제습기: dehumidifier
     * 난방기: heater
     * 가습기: humidifier
     * 환풍기: ventilator
     */
    @Column(nullable = false, length = 50)
    @Comment("센서 종류")
    private String sensorType;

    @Column(nullable = false)
    @Comment("센서 상태")
    private Boolean sensorState;

    @Column(nullable = false, length = 100)
    @Comment("설치 장소")
    private String location;

    protected Sensor() {}

    public Sensor(String sensorName, String sensorType, Boolean sensorState, String location) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.sensorState = sensorState;
        this.location = location;
    }

    public void update(String sensorName, String sensorType) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
    }

    public void updateState(Boolean sensorState) {
        this.sensorState = sensorState;
    }
}
