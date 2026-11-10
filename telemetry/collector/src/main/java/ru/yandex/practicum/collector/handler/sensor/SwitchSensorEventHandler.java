
package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.SwitchSensorEventDto;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final SensorEventMapper sensorEventMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public SwitchSensorEventHandler(
            SensorEventMapper sensorEventMapper,
            KafkaEventProducer kafkaEventProducer
    ) {
        this.sensorEventMapper = sensorEventMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public void handle(SensorEventProto event) {
        SwitchSensorEventDto dto = new SwitchSensorEventDto();

        dto.setId(event.getId());
        dto.setHubId(event.getHubId());
        dto.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));

        dto.setState(event.getSwitchSensor().getState());

        kafkaEventProducer.sendSensorEvent(
                sensorEventMapper.toAvro(dto)
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}
