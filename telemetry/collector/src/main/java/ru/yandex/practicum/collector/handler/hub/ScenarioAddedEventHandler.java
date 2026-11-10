package telemetry.collector.handler.hub;

import org.apache.kafka.clients.consumer.internals.events.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;

public class ScenarioAddedEventHandler implements HubEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ScenarioAddedEventHandler.class);

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void hanlde(HubEventProto event) {
        ScenarioAddedEventProto scenario = event.getScenarioAdded();
        log.info("Добавлен сценарий: {}", scenario);
    }
}
