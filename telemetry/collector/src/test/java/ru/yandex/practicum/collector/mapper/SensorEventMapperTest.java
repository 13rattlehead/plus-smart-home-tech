package ru.yandex.practicum.collector.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.collector.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SensorEventMapperTest {

    private final SensorEventMapper mapper = new SensorEventMapper();

    @Test
    void shouldMapLightSensorEvent() {
        LightSensorEventDto dto = new LightSensorEventDto();
        dto.setId("sensor-light-1");
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setLinkQuality(75);
        dto.setLuminosity(59);

        SensorEventAvro result = mapper.toAvro(dto);

        assertEquals("sensor-light-1", result.getId().toString());
        assertEquals("hub-1", result.getHubId().toString());
        assertEquals(dto.getTimestamp(), result.getTimestamp());

        LightSensorAvro payload =
                assertInstanceOf(LightSensorAvro.class, result.getPayload());

        assertEquals(75, payload.getLinkQuality());
        assertEquals(59, payload.getLuminosity());
    }

    @Test
    void shouldMapTemperatureSensorEvent() {
        TemperatureSensorEventDto dto = new TemperatureSensorEventDto();
        dto.setId("sensor-temperature-1");
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setTemperatureC(22);
        dto.setTemperatureF(72);

        SensorEventAvro result = mapper.toAvro(dto);

        assertEquals("sensor-temperature-1", result.getId().toString());
        assertEquals("hub-1", result.getHubId().toString());

        TemperatureSensorAvro payload =
                assertInstanceOf(TemperatureSensorAvro.class, result.getPayload());

        assertEquals(22, payload.getTemperatureC());
        assertEquals(72, payload.getTemperatureF());
    }


    @Test
    void shouldMapMotionSensorEvent() {
        MotionSensorEventDto dto = new MotionSensorEventDto();
        dto.setId("sensor-motion-1");
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setLinkQuality(80);
        dto.setMotion(true);
        dto.setVoltage(95);

        SensorEventAvro result = mapper.toAvro(dto);

        MotionSensorAvro payload =
                assertInstanceOf(MotionSensorAvro.class, result.getPayload());

        assertEquals("sensor-motion-1", result.getId().toString());
        assertEquals("hub-1", result.getHubId().toString());
        assertEquals(80, payload.getLinkQuality());
        assertEquals(true, payload.getMotion());
        assertEquals(95, payload.getVoltage());
    }

    @Test
    void shouldMapSwitchSensorEvent() {
        SwitchSensorEventDto dto = new SwitchSensorEventDto();
        dto.setId("sensor-switch-1");
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setState(true);

        SensorEventAvro result = mapper.toAvro(dto);

        SwitchSensorAvro payload =
                assertInstanceOf(SwitchSensorAvro.class, result.getPayload());

        assertEquals("sensor-switch-1", result.getId().toString());
        assertEquals(true, payload.getState());
    }

    @Test
    void shouldMapClimateSensorEvent() {
        ClimateSensorEventDto dto = new ClimateSensorEventDto();
        dto.setId("sensor-climate-1");
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setTemperatureC(23);
        dto.setHumidity(45);
        dto.setCo2Level(600);

        SensorEventAvro result = mapper.toAvro(dto);

        ClimateSensorAvro payload =
                assertInstanceOf(ClimateSensorAvro.class, result.getPayload());

        assertEquals("sensor-climate-1", result.getId().toString());
        assertEquals(23, payload.getTemperatureC());
        assertEquals(45, payload.getHumidity());
        assertEquals(600, payload.getCo2Level());
    }

}
