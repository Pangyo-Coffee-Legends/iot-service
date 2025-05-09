package com.nhnacademy.iot_service.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

/**
 * 센서 정보를 담는 JPA 엔티티 클래스입니다.
 * 센서 번호, 이름, 종류, 상태, 설치 장소 정보를 포함합니다.
 */
@Entity
@Table(name = "sensors")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Comment("센서 번호")
    private Long sensorNo;

    @Column(nullable = false, length = 50)
    @Comment("센서 이름")
    private String sensorName;

    @Column(nullable = false, length = 50)
    @Comment("센서 종류")
    private String sensorType;

    @Column(nullable = false)
    @Comment("센서 상태")
    private Boolean sensorStatus;

    @Column(nullable = false, length = 100)
    @Comment("설치 장소")
    private String location;

    protected Sensor() {}

    public Sensor(String sensorName, String sensorType, Boolean sensorStatus, String location) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.sensorStatus = sensorStatus;
        this.location = location;
    }

    public Long getSensorNo() {
        return sensorNo;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public Boolean getSensorStatus() {
        return sensorStatus;
    }

    public String getLocation() {
        return location;
    }

    /**
     * 센서 상태를 수정합니다.
     *
     * @param sensorStatus true: 정상, false: 비정상
     */
    public void setSensorStatus(Boolean sensorStatus) {
        this.sensorStatus = sensorStatus;
    }
}
