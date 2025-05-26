package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.SensorMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorMbMappingRepository extends JpaRepository<SensorMemberMapping, Long> {
}
