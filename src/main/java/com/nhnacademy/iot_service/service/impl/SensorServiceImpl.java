package com.nhnacademy.iot_service.service.impl;

import com.nhnacademy.iot_service.dto.SensorCreateRequestDto;
import com.nhnacademy.iot_service.dto.SensorResponseDto;
import com.nhnacademy.iot_service.dto.Sensor;
import com.nhnacademy.iot_service.repository.SensorRepository;
import com.nhnacademy.iot_service.service.SensorService;
import org.springframework.stereotype.Service;

/**
 * SensorService 인터페이스의 구현체입니다.
 * 센서 등록 및 조회 로직을 실제로 처리합니다.
 */
@Service
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    /**
     * SensorServiceImpl 생성자입니다.
     *
     * @param sensorRepository 센서 저장소 (JPA 인터페이스)
     */
    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    /**
     * 센서를 등록하고 저장된 결과를 반환합니다.
     *
     * @param dto 센서 생성 요청 DTO
     * @return 생성된 센서의 응답 DTO
     */
    @Override
    public SensorResponseDto createSensor(SensorCreateRequestDto dto) {
        Sensor sensor = new Sensor(
                dto.getSensorName(),
                dto.getSensorType(),
                dto.getSensorStatus(),
                dto.getLocation()
        );
        Sensor saved = sensorRepository.save(sensor);
        return SensorResponseDto.from(saved);
    }

    /**
     * 센서 번호를 기반으로 센서를 조회하고 응답 DTO로 반환합니다.
     *
     * @param sensorNo 센서 고유 번호
     * @return 조회된 센서의 응답 DTO
     */
    @Override
    public SensorResponseDto getSensor(Long sensorNo) {
        Sensor sensor = sensorRepository.findById(sensorNo)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));
        return SensorResponseDto.from(sensor);
    }
}
