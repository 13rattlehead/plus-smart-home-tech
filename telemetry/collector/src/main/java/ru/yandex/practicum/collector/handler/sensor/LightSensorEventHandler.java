package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.LightSensorEventDto;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class LightSensorEventHandler implements SensorEventHandler {

    private final SensorEventMapper sensorEventMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public LightSensorEventHandler(
            SensorEventMapper sensorEventMapper,
            KafkaEventProducer kafkaEventProducer
    ) {
        this.sensorEventMapper = sensorEventMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorEventDto dto = new LightSensorEventDto();

        dto.setId(event.getId());
        dto.setHubId(event.getHubId());
        dto.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));

        dto.setLinkQuality(event.getLightSensor().getLinkQuality());
        dto.setLuminosity(event.getLightSensor().getLuminosity());

        kafkaEventProducer.sendSensorEvent(
                sensorEventMapper.toAvro(dto)
        );
    }
}