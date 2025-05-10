package com.nhnacademy.iot_service;

import com.nhnacademy.iot_service.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class IotServiceApplicationTests {

    @MockitoBean
    SensorRepository sensorRepository;

    @Test
    void contextLoads() {
    }

}
