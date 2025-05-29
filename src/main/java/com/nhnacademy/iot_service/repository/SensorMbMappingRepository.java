package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.SensorMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 센서-회원 매핑(SensorMemberMapping) 엔티티에 대한 JPA 리포지토리 인터페이스입니다.
 * <p>
 * 기본적인 CRUD 및 페이징, 정렬 기능을 제공합니다.
 * </p>
 */
public interface SensorMbMappingRepository extends JpaRepository<SensorMemberMapping, Long> {
}
