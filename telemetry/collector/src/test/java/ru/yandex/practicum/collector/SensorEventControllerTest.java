package ru.yandex.practicum.collector;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.collector.controller.SensorEventController;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SensorEventController.class)
class SensorEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorEventMapper mapper;

    @MockBean
    private KafkaEventProducer producer;

    @Test
    void shouldAcceptValidSensorEvent() throws Exception {
        mockMvc.perform(post("/events/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "sensor-1",
                                  "hubId": "hub-1",
                                  "timestamp": "2026-10-21T15:00:00Z",
                                  "linkQuality": 75,
                                  "luminosity": 59,
                                  "type": "LIGHT_SENSOR_EVENT"
                                }
                                """))
                .andExpect(status().isOk());

        verify(mapper).toAvro(any());
        verify(producer).sendSensorEvent(any());
    }

    @Test
    void shouldRejectSensorEventWithoutId() throws Exception {
        mockMvc.perform(post("/events/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hubId": "hub-1",
                                  "type": "LIGHT_SENSOR_EVENT",
                                  "linkQuality": 75,
                                  "luminosity": 59
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(mapper, never()).toAvro(any());
        verify(producer, never()).sendSensorEvent(any());
    }

    @Test
    void shouldRejectUnknownSensorEventType() throws Exception {
        mockMvc.perform(post("/events/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "sensor-1",
                                  "hubId": "hub-1",
                                  "type": "UNKNOWN_EVENT"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(mapper, never()).toAvro(any());
        verify(producer, never()).sendSensorEvent(any());
    }
}
