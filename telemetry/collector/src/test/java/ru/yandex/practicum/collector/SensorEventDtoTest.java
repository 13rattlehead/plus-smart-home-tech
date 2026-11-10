package ru.yandex.practicum.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.collector.dto.sensor.LightSensorEventDto;
import ru.yandex.practicum.collector.dto.sensor.SensorEventDto;

import static org.junit.jupiter.api.Assertions.*;

class SensorEventDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Test
    void shouldDeserializeLightSensorEvent() throws Exception {
        String json = """
                {
                    "id": "sensor.light.3",
                    "hubId": "hub-2",
                    "timestamp": "2026-10-21T14:00:00Z",
                    "linkQuality": 75,
                    "luminosity": 59,
                    "type": "LIGHT_SENSOR_EVENT"
                }
                """;

        SensorEventDto event =
                objectMapper.readValue(json, SensorEventDto.class);

        assertInstanceOf(LightSensorEventDto.class, event);

        LightSensorEventDto lightEvent = (LightSensorEventDto) event;
        assertEquals("sensor.light.3", lightEvent.getId());
        assertEquals("hub-2", lightEvent.getHubId());
        assertEquals(75, lightEvent.getLinkQuality());
        assertEquals(59, lightEvent.getLuminosity());
    }


    @Test
    void shouldSetTimestampWhenMissing() throws Exception {
        String json = """
            {
                "id": "sensor.light.3",
                "hubId": "hub-2",
                "linkQuality": 75,
                "luminosity": 59,
                "type": "LIGHT_SENSOR_EVENT"
            }
            """;

        SensorEventDto event =
                objectMapper.readValue(json, SensorEventDto.class);

        assertNotNull(event.getTimestamp());
    }


    @Test
    void shouldFailValidationWhenIdIsBlank() throws Exception {
        String json = """
            {
                "id": "",
                "hubId": "hub-2",
                "timestamp": "2026-10-21T14:00:00Z",
                "linkQuality": 75,
                "luminosity": 59,
                "type": "LIGHT_SENSOR_EVENT"
            }
            """;

        SensorEventDto event =
                objectMapper.readValue(json, SensorEventDto.class);

        var validator = jakarta.validation.Validation
                .buildDefaultValidatorFactory()
                .getValidator();

        assertFalse(validator.validate(event).isEmpty());
    }


}
