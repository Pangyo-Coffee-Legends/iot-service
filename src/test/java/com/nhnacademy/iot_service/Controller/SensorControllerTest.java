package com.nhnacademy.iot_service.Controller;

import com.nhnacademy.iot_service.controller.SensorController;
import com.nhnacademy.iot_service.dto.sensor.SensorRegisterRequest;
import com.nhnacademy.iot_service.dto.sensor.SensorResponse;
import com.nhnacademy.iot_service.service.SensorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorControllerTest {
    @Mock
    private SensorService sensorService;

    @InjectMocks
    private SensorController sensorController;

    @Test
    @DisplayName("registerSensor 메서드가 SensorService와 연동되어 정상 동작하는지 테스트")
    void registerSensor_callsServiceAndReturnsResponse() {
        SensorRegisterRequest request = mock(SensorRegisterRequest.class);
        SensorResponse expectedResponse = mock(SensorResponse.class);

        when(sensorService.registerSensor(request)).thenReturn(expectedResponse);

        ResponseEntity<SensorResponse> responseEntity = sensorController.registerSensor(request);

        verify(sensorService, times(1)).registerSensor(request);
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    @DisplayName("getSensor 메서드가 SensorService와 연동되어 정상 동작하는지 테스트")
    void getSensor_callsServiceAndReturnsResponse() {
        Long sensorNo = 1L;
        SensorResponse expectedResponse = mock(SensorResponse.class);

        when(sensorService.getSensor(sensorNo)).thenReturn(expectedResponse);

        ResponseEntity<SensorResponse> responseEntity = sensorController.getSensor(sensorNo);

        verify(sensorService, times(1)).getSensor(sensorNo);
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    @DisplayName("getSensors 메서드가 SensorService와 연동되어 정상 동작하는지 테스트")
    void getSensors_callsServiceAndReturnsResponseList() {
        String sensorPlace = "회의실";
        List<SensorResponse> expectedList = List.of(mock(SensorResponse.class), mock(SensorResponse.class));

        when(sensorService.getSensorByLocation(sensorPlace)).thenReturn(expectedList);

        ResponseEntity<List<SensorResponse>> responseEntity = sensorController.getSensors(sensorPlace);

        verify(sensorService, times(1)).getSensorByLocation(sensorPlace);
        assertEquals(expectedList, responseEntity.getBody());
    }
}
