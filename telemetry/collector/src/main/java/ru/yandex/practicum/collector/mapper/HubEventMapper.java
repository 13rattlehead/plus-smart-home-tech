
package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.Objects;

@Component
public class HubEventMapper {

    public HubEventAvro toAvro(HubEventDto dto) {
        Objects.requireNonNull(dto, "Событие хаба не может быть null");

        Object payload;

        if (dto instanceof DeviceAddedEventDto event) {
            payload = DeviceAddedEventAvro.newBuilder()
                    .setId(event.getId())
                    .setType(DeviceTypeAvro.valueOf(event.getDeviceType().name()))
                    .build();

        } else if (dto instanceof DeviceRemovedEventDto event) {
            payload = DeviceRemovedEventAvro.newBuilder()
                    .setId(event.getId())
                    .build();

        } else if (dto instanceof ScenarioAddedEventDto event) {
            payload = ScenarioAddedEventAvro.newBuilder()
                    .setName(event.getName())
                    .setConditions(event.getConditions().stream()
                            .map(this::toAvro)
                            .toList())
                    .setActions(event.getActions().stream()
                            .map(this::toAvro)
                            .toList())
                    .build();

        } else if (dto instanceof ScenarioRemovedEventDto event) {
            payload = ScenarioRemovedEventAvro.newBuilder()
                    .setName(event.getName())
                    .build();

        } else {
            throw new IllegalArgumentException(
                    "Неизвестный тип события хаба: " + dto.getClass().getName()
            );
        }

        return HubEventAvro.newBuilder()
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private ScenarioConditionAvro toAvro(ScenarioConditionDto dto) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(dto.getSensorId())
                .setType(ConditionTypeAvro.valueOf(dto.getType().name()))
                .setOperation(
                        ConditionOperationAvro.valueOf(dto.getOperation().name())
                )
                .setValue(dto.getValue())
                .build();
    }

    private DeviceActionAvro toAvro(DeviceActionDto dto) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(dto.getSensorId())
                .setType(ActionTypeAvro.valueOf(dto.getType().name()))
                .setValue(dto.getValue())
                .build();
    }
}
