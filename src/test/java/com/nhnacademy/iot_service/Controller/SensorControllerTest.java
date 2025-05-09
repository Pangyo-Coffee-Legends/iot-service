package com.nhnacademy.iot_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.iot_service.controller.SensorController;
import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.dto.SensorResponse;
import com.nhnacademy.iot_service.service.SensorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorController.class)
class SensorControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SensorService sensorService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/sensors - 센서 전체 목록 조회")
    void testGetAllSensors() throws Exception {
        Sensor sensor = new Sensor("제습센서", "dehumidifier", true, "서버실");
        SensorResponse dto = SensorResponse.from(sensor);

        when(sensorService.getAllSensors()).thenReturn(List.of(sensor));

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sensorName").value("제습센서"))
                .andExpect(jsonPath("$[0].sensorStatus").value(true));
    }

    @Test
    @DisplayName("PATCH /api/sensors/{sensorNo}?status=true - 센서 상태 변경")
    void testUpdateSensorStatus() throws Exception {
        Sensor updatedSensor = new Sensor("에어컨센서", "aircon", true, "회의실");
        updatedSensor.setSensorStatus(true);

        when(sensorService.updateSensorStatus(1L, true)).thenReturn(updatedSensor);

        mockMvc.perform(patch("/api/sensors/1?status=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorName").value("에어컨센서"))
                .andExpect(jsonPath("$.sensorStatus").value(true));
    }

    @Test
    @DisplayName("POST /api/sensors/process - 센서 처리 요청")
    void testProcessSensorData() throws Exception {
        Sensor sensor = new Sensor("온도센서", "temperature", false, "회의실");

        mockMvc.perform(post("/api/sensors/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensor)))
                .andExpect(status().isOk());

        verify(sensorService).processSensorData(any(Sensor.class));
    }
}
