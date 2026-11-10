
package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.TemperatureSensorEventDto;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final SensorEventMapper sensorEventMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public TemperatureSensorEventHandler(
            SensorEventMapper sensorEventMapper,
            KafkaEventProducer kafkaEventProducer
    ) {
        this.sensorEventMapper = sensorEventMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public void handle(SensorEventProto event) {
        TemperatureSensorEventDto dto = new TemperatureSensorEventDto();

        dto.setId(event.getId());
        dto.setHubId(event.getHubId());
        dto.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));

        dto.setTemperatureC(event.getTemperatureSensor().getTemperatureC());
        dto.setTemperatureF(event.getTemperatureSensor().getTemperatureF());

        kafkaEventProducer.sendSensorEvent(
                sensorEventMapper.toAvro(dto)
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }
}
