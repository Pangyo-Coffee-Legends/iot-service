package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.dto.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 센서 데이터를 데이터베이스에 저장하고 조회하기 위한 JPA 리포지토리입니다.
 * Spring Data JPA가 자동으로 구현체를 생성해줍니다.
 *
 * JpaRepository<Sensor, Long>을 상속하여
 * 기본적인 CRUD (Create, Read, Update, Delete) 기능을 제공합니다.
 */
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    // 필요한 경우 사용자 정의 쿼리 메서드를 추가할 수 있습니다.
}
