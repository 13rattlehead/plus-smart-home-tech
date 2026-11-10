
package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.sensor.ClimateSensorEventDto;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final SensorEventMapper sensorEventMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public ClimateSensorEventHandler(
            SensorEventMapper sensorEventMapper,
            KafkaEventProducer kafkaEventProducer
    ) {
        this.sensorEventMapper = sensorEventMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public void handle(SensorEventProto event) {
        ClimateSensorEventDto dto = new ClimateSensorEventDto();

        dto.setId(event.getId());
        dto.setHubId(event.getHubId());
        dto.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));

        dto.setTemperatureC(event.getClimateSensor().getTemperatureC());
        dto.setHumidity(event.getClimateSensor().getHumidity());
        dto.setCo2Level(event.getClimateSensor().getCo2Level());

        kafkaEventProducer.sendSensorEvent(
                sensorEventMapper.toAvro(dto)
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }
}
