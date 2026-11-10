package ru.yandex.practicum.collector.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;

@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ScenarioRemovedEventHandler.class);

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void hanlde(HubEventProto event) {
        ScenarioRemovedEventProto scenario = event.getScenarioRemoved();
        log.info("Удалён сценарий: {}", scenario);
    }
}
