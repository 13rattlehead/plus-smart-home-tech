
package ru.yandex.practicum.collector.mapper;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.collector.dto.enums.DeviceType;
import ru.yandex.practicum.collector.dto.hub.DeviceAddedEventDto;
import ru.yandex.practicum.collector.dto.hub.DeviceRemovedEventDto;
import ru.yandex.practicum.collector.dto.hub.ScenarioRemovedEventDto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class HubEventMapperTest {

    private final HubEventMapper mapper = new HubEventMapper();

    @Test
    void shouldMapDeviceAddedEvent() {
        DeviceAddedEventDto dto = new DeviceAddedEventDto();
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setId("sensor-1");
        dto.setDeviceType(DeviceType.MOTION_SENSOR);

        HubEventAvro result = mapper.toAvro(dto);

        assertEquals("hub-1", result.getHubId().toString());
        assertEquals(dto.getTimestamp(), result.getTimestamp());

        DeviceAddedEventAvro payload =
                assertInstanceOf(DeviceAddedEventAvro.class, result.getPayload());

        assertEquals("sensor-1", payload.getId().toString());
        assertEquals(DeviceTypeAvro.MOTION_SENSOR, payload.getType());
    }


    @Test
    void shouldMapDeviceRemovedEvent() {
        DeviceRemovedEventDto dto = new DeviceRemovedEventDto();
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setId("sensor-1");

        HubEventAvro result = mapper.toAvro(dto);

        assertEquals("hub-1", result.getHubId().toString());
        assertEquals(dto.getTimestamp(), result.getTimestamp());

        DeviceRemovedEventAvro payload =
                assertInstanceOf(DeviceRemovedEventAvro.class, result.getPayload());

        assertEquals("sensor-1", payload.getId().toString());
    }

    @Test
    void shouldMapScenarioRemovedEvent() {
        ScenarioRemovedEventDto dto = new ScenarioRemovedEventDto();
        dto.setHubId("hub-1");
        dto.setTimestamp(Instant.parse("2026-10-21T15:00:00Z"));
        dto.setName("evening-light");

        HubEventAvro result = mapper.toAvro(dto);

        assertEquals("hub-1", result.getHubId().toString());
        assertEquals(dto.getTimestamp(), result.getTimestamp());

        ScenarioRemovedEventAvro payload =
                assertInstanceOf(ScenarioRemovedEventAvro.class, result.getPayload());

        assertEquals("evening-light", payload.getName().toString());
    }

}
