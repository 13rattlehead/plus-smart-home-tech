
package ru.yandex.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.DeviceAddedEventDto;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import com.google.protobuf.Timestamp;

import java.time.Instant;

@Component
public class DeviceAddedEventHandler implements HubEventHandler {

    private final HubEventMapper hubEventMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public DeviceAddedEventHandler(HubEventMapper hubEventMapper,
                                   KafkaEventProducer kafkaEventProducer) {
        this.hubEventMapper = hubEventMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        DeviceAddedEventDto dto = new DeviceAddedEventDto();

        dto.setHubId(event.getId());

        Timestamp timestamp = event.getTimestamp();
        dto.setTimestamp(Instant.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos()
        ));

        dto.setId(event.getDeviceAdded().getId());
        dto.setDeviceType(
                ru.yandex.practicum.collector.dto.enums.DeviceType.valueOf(
                        event.getDeviceAdded().getType().name()
                )
        );

        kafkaEventProducer.sendHubEvent(hubEventMapper.toAvro(dto));
    }
}
