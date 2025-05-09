package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    // 필요한 경우 메서드 추가 (예: findByLocation, findBySensorName 등)
}
