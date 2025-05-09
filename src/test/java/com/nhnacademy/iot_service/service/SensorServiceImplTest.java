package com.nhnacademy.iot_service.service;

import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.repository.SensorRepository;
import com.nhnacademy.iot_service.service.SensorService;
import com.nhnacademy.iot_service.service.impl.SensorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class SensorServiceImplTest {

    private SensorRepository sensorRepository;
    private SensorService sensorService;

    @BeforeEach
    void setUp() {
        sensorRepository = mock(SensorRepository.class);
        sensorService = new SensorServiceImpl(sensorRepository);
    }

    @Test
    @DisplayName("자동 제어 - 센서 이름과 위치가 일치하면 상태 ON 저장")
    void testProcessSensorData() {
        Sensor incoming = new Sensor("온도센서", "temperature", false, "회의실");

        Sensor stored = new Sensor("온도센서", "temperature", false, "회의실");
        stored.setSensorStatus(false);

        when(sensorRepository.findAll()).thenReturn(List.of(stored));

        sensorService.processSensorData(incoming);

        assertThat(stored.getSensorStatus()).isTrue();  // 상태가 true로 바뀌어야 함
        verify(sensorRepository, times(1)).save(stored);
    }

    @Test
    @DisplayName("센서 상태 수동 업데이트 성공")
    void testUpdateSensorStatus() {
        Sensor existing = new Sensor("제습센서", "dehumidifier", false, "서버실");
        existing.setSensorStatus(false);
        Long sensorNo = 1L;

        when(sensorRepository.findById(sensorNo)).thenReturn(Optional.of(existing));
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sensor updated = sensorService.updateSensorStatus(sensorNo, true);

        assertThat(updated.getSensorStatus()).isTrue();
        verify(sensorRepository).save(existing);
    }

    @Test
    @DisplayName("센서 상태 변경 실패 - 센서 없음")
    void testUpdateSensorStatus_notFound() {
        when(sensorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sensorService.updateSensorStatus(99L, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sensor not found");
    }

    @Test
    @DisplayName("전체 센서 목록 조회")
    void testGetAllSensors() {
        when(sensorRepository.findAll()).thenReturn(List.of(
                new Sensor("센서1", "type1", true, "장소1"),
                new Sensor("센서2", "type2", false, "장소2")
        ));

        List<Sensor> result = sensorService.getAllSensors();

        assertThat(result).hasSize(2);
        verify(sensorRepository).findAll();
    }
}
