
package ru.yandex.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.hub.DeviceActionDto;
import ru.yandex.practicum.collector.dto.hub.HubEventDto;
import ru.yandex.practicum.collector.dto.hub.ScenarioAddedEventDto;
import ru.yandex.practicum.collector.dto.hub.ScenarioConditionDto;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;

import java.time.Instant;

@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final HubEventMapper hubEventMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public ScenarioAddedEventHandler(
            HubEventMapper hubEventMapper,
            KafkaEventProducer kafkaEventProducer
    ) {
        this.hubEventMapper = hubEventMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioAddedEventProto scenario = event.getScenarioAdded();

        ScenarioAddedEventDto dto = new ScenarioAddedEventDto();
        dto.setHubId(event.getId());
        dto.setTimestamp(Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        ));
        dto.setName(scenario.getName());

        dto.setConditions(scenario.getConditionList().stream()
                .map(condition -> {
                    ScenarioConditionDto conditionDto = new ScenarioConditionDto();
                    conditionDto.setSensorId(condition.getId());
                    conditionDto.setType(
                            ru.yandex.practicum.collector.dto.enums.ConditionType
                                    .valueOf(condition.getType().name())
                    );
                    conditionDto.setOperation(
                            ru.yandex.practicum.collector.dto.enums.ConditionOperation
                                    .valueOf(condition.getOperation().name())
                    );
                    switch (condition.getValueCase()) {
                        case BOOL_VALUE -> conditionDto.setValue(condition.getBoolValue());
                        case INT_VALUE -> conditionDto.setValue(condition.getIntValue());
                        case VALUE_NOT_SET -> conditionDto.setValue(null);
                    }
                    return conditionDto;
                })
                .toList());

        dto.setActions(scenario.getActionList().stream()
                .map(action -> {
                    DeviceActionDto actionDto = new DeviceActionDto();
                    actionDto.setSensorId(action.getSensorId());
                    actionDto.setType(
                            ru.yandex.practicum.collector.dto.enums.ActionType
                                    .valueOf(action.getType().name())
                    );
                    actionDto.setValue(action.getValue());
                    return actionDto;
                })
                .toList());

        kafkaEventProducer.sendHubEvent(hubEventMapper.toAvro(dto));
    }
}
