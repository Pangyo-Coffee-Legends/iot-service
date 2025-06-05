package com.nhnacademy.iot_service.service;

import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.dto.sensor.SensorResult;
import com.nhnacademy.iot_service.dto.sensor.SensorUpdateRequest;

import java.util.List;

/**
 * 센서 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 */
public interface SensorService {
    /**
     * 센서를 등록합니다.
     *
     * @param request 센서 등록 요청 정보
     * @return 등록된 센서의 응답 정보
     */
    SensorResponse registerSensor(SensorRegisterRequest request);

    /**
     * 센서 정보를 수정합니다.
     *
     * @param sensorNo 수정할 센서의 고유 번호
     * @param request  센서 수정 요청 정보
     * @return 수정된 센서의 응답 정보
     */
    SensorResponse updateSensor(Long sensorNo, SensorUpdateRequest request);

    /**
     * 센서를 삭제합니다.
     *
     * @param sensorNo 삭제할 센서의 고유 번호
     */
    void deleteSensor(Long sensorNo);

    /**
     * 센서 정보를 조회합니다.
     *
     * @param sensorNo 조회할 센서의 고유 번호
     * @return 센서의 응답 정보
     */
    SensorResponse getSensor(Long sensorNo);

    /**
     * 특정 위치에 있는 센서 목록을 조회합니다.
     *
     * @param location 센서 위치
     * @return 해당 위치의 센서 응답 정보 리스트
     */
    List<SensorResponse> getSensorByLocation(String location);

    /**
     * 센서 번호와 AI에서 받은 추가 정보를 이용하여 센서 상태를 평가하고 결과를 반환합니다.
     *
     * <p>
     * 이 메서드는 지정된 센서 번호에 해당하는 센서 정보를 데이터베이스에서 조회한 뒤,
     * AI(또는 외부 시스템)에서 전달받은 facts와 합쳐서 룰 엔진에 전달합니다.
     * 룰 엔진의 평가 결과를 바탕으로 센서의 on/off 상태와 상세 평가 결과를 포함한
     * {@link SensorResult} 객체를 반환합니다.
     * </p>
     *
     * @param sensorNo 평가할 센서의 고유 번호 (PK)
     * @return 센서의 상태 및 룰 평가 결과를 담은 {@link SensorResult}
     */
    SensorResult getSensorStatus(Long sensorNo);

    /**
     * 특정 센서의 위치에 설치된 모든 센서의 상태를 조회합니다.
     *
     * <p>
     * 전달받은 센서 번호(sensorNo)를 기준으로 해당 센서를 조회한 뒤,
     * 그 센서와 동일한 위치(location)에 설치된 모든 센서들을 데이터베이스에서 검색합니다.
     * 이후 룰 엔진을 실행하여 각 센서 타입(냉방기, 제습기, 난방기, 가습기, 환풍기 등)에 대해
     * ON/OFF 상태를 판단하고, 각 센서별로 평가 결과와 함께 {@link SensorResult} 객체로 반환합니다.
     * </p>
     *
     * @param sensorNo 기준이 되는 센서의 고유 번호 (PK)
     * @return 동일 위치에 설치된 각 센서의 상태와 평가 결과를 담은 {@link SensorResult} 리스트
     */
    List<SensorResult> getSensorStatusByLocation(Long sensorNo);

    /**
     * 센서 장소 리스트 조회
     * @return 센서 장소 리스트 조회
     */
    List<String> getAllSensorLocations();
}
