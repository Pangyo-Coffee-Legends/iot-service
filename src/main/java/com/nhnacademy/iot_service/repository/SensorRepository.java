package com.nhnacademy.iot_service.repository;

import com.nhnacademy.iot_service.domain.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 센서(Sensor) 엔티티에 대한 JPA 리포지토리 인터페이스입니다.
 * <p>
 * 기본적인 CRUD 연산과 위치(location)로 센서 목록을 조회하는 메서드를 제공합니다.
 * </p>
 */
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    /**
     * 주어진 위치(location)에 해당하는 센서 목록을 조회합니다.
     *
     * @param location 센서 위치
     * @return 해당 위치의 센서 리스트
     */
    List<Sensor> findByLocation(String location);

    /**
     * 센서의 장소들에 대한 정보를 조회합니다.
     * @return 센서 위치 리스트
     */
    @Query("SELECT DISTINCT s.location FROM Sensor s")
    List<String> findDistinctLocations();
}