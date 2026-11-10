package ru.yandex.practicum.collector;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.collector.controller.HubEventController;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HubEventController.class)
class HubEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HubEventMapper mapper;

    @MockBean
    private KafkaEventProducer producer;

    @Test
    void shouldAcceptValidDeviceAddedEvent() throws Exception {
        mockMvc.perform(post("/events/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hubId": "hub-1",
                                  "timestamp": "2026-10-21T15:00:00Z",
                                  "id": "sensor-1",
                                  "deviceType": "MOTION_SENSOR",
                                  "type": "DEVICE_ADDED"
                                }
                                """))
                .andExpect(status().isOk());

        verify(mapper).toAvro(any());
        verify(producer).sendHubEvent(any());
    }

    @Test
    void shouldRejectHubEventWithoutHubId() throws Exception {
        mockMvc.perform(post("/events/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "timestamp": "2026-10-21T15:00:00Z",
                                  "id": "sensor-1",
                                  "deviceType": "MOTION_SENSOR",
                                  "type": "DEVICE_ADDED"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(mapper, never()).toAvro(any());
        verify(producer, never()).sendHubEvent(any());
    }

    @Test
    void shouldRejectUnknownHubEventType() throws Exception {
        mockMvc.perform(post("/events/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hubId": "hub-1",
                                  "type": "UNKNOWN_EVENT"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(mapper, never()).toAvro(any());
        verify(producer, never()).sendHubEvent(any());
    }
}
