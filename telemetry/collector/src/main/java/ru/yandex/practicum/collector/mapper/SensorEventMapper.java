package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.Objects;


@Component
public class SensorEventMapper {

    public SensorEventAvro toAvro(SensorEventDto dto) {
        Objects.requireNonNull(dto, "Событие датчика не может быть null");

        Object payload;

        if (dto instanceof LightSensorEventDto event) {
            payload = LightSensorAvro.newBuilder()
                    .setLinkQuality(Objects.requireNonNull(event.getLinkQuality()))
                    .setLuminosity(Objects.requireNonNull(event.getLuminosity()))
                    .build();

        } else if (dto instanceof TemperatureSensorEventDto event) {
            payload = TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(Objects.requireNonNull(event.getTemperatureC()))
                    .setTemperatureF(Objects.requireNonNull(event.getTemperatureF()))
                    .build();

        } else if (dto instanceof MotionSensorEventDto event) {
            payload = MotionSensorAvro.newBuilder()
                    .setLinkQuality(Objects.requireNonNull(event.getLinkQuality()))
                    .setMotion(Objects.requireNonNull(event.getMotion()))
                    .setVoltage(Objects.requireNonNull(event.getVoltage()))
                    .build();

        } else if (dto instanceof SwitchSensorEventDto event) {
            payload = SwitchSensorAvro.newBuilder()
                    .setState(Objects.requireNonNull(event.getState()))
                    .build();

        } else if (dto instanceof ClimateSensorEventDto event) {
            payload = ClimateSensorAvro.newBuilder()
                    .setTemperatureC(Objects.requireNonNull(event.getTemperatureC()))
                    .setHumidity(Objects.requireNonNull(event.getHumidity()))
                    .setCo2Level(Objects.requireNonNull(event.getCo2Level()))
                    .build();

        } else {
            throw new IllegalArgumentException(
                    "Неизвестный тип события датчика: " + dto.getClass().getName()
            );
        }

        return SensorEventAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }
}
