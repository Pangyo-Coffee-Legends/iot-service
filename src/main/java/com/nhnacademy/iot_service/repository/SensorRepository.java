package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByLocation(String location);
}
