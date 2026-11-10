package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.dto.sensor.LightSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class SensorEventController {

    private final SensorEventMapper mapper;
    private final KafkaEventProducer producer;

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(
            @Valid @RequestBody SensorEventDto event
    ) {

        producer.sendSensorEvent(mapper.toAvro(event));
        return ResponseEntity.ok().build();
    }
}
