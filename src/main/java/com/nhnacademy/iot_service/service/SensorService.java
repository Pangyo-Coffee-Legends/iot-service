package com.nhnacademy.iot_service.service;

import com.nhnacademy.iot_service.dto.SensorCreateRequestDto;
import com.nhnacademy.iot_service.dto.SensorResponseDto;

/**
 * 센서 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * 센서 등록, 조회 등 기능의 명세를 정의합니다.
 */
public interface SensorService {

    /**
     * 센서를 등록합니다.
     *
     * @param dto 센서 생성 요청 DTO
     * @return 생성된 센서의 응답 DTO
     */
    SensorResponseDto createSensor(SensorCreateRequestDto dto);

    /**
     * 센서를 고유 번호로 조회합니다.
     *
     * @param sensorNo 센서 고유 번호
     * @return 조회된 센서의 응답 DTO
     */
    SensorResponseDto getSensor(Long sensorNo);
}
