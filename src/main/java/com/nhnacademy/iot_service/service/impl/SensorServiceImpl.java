package com.nhnacademy.iot_service.service.impl;

import com.nhnacademy.iot_service.domain.Sensor;
import com.nhnacademy.iot_service.repository.SensorRepository;
import com.nhnacademy.iot_service.service.SensorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {
    private final SensorRepository sensorRepository;

    public SensorServiceImpl(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public void processSensorData(Sensor incomingSensor) {
        List<Sensor> sensors = sensorRepository.findAll();

        for (Sensor sensor : sensors) {
            if (sensor.getSensorName().equals(incomingSensor.getSensorName()) &&
                    sensor.getLocation().equals(incomingSensor.getLocation())) {
                System.out.printf("%s의 %s 제어기기 작동%n", sensor.getLocation(), sensor.getSensorName());

                sensor.setSensorStatus(true);
                sensorRepository.save(sensor);
            }
        }
    }

    @Override
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @Override
    public Sensor updateSensorStatus(Long sensorNo, boolean status) {
        Sensor sensor = sensorRepository.findById(sensorNo)
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found"));

        sensor.setSensorStatus(status); // 상태만 수정
        return sensorRepository.save(sensor);
    }
}
