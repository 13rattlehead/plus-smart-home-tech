package ru.yandex.practicum.analyzer.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import com.google.protobuf.Timestamp;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.entity.*;
import ru.yandex.practicum.analyzer.repository.*;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class SnapshotProcessor {
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final ScenarioRepository scenarios;
    private final SensorRepository sensors;
    private final HubRouterControllerBlockingStub hubRouter;
    private final String topic;

    public SnapshotProcessor(KafkaConsumer<String, SensorsSnapshotAvro> consumer, ScenarioRepository scenarios,
                             SensorRepository sensors, @GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouter,
                             @Value("${analyzer.kafka.topics.snapshots}") String topic) {
        this.consumer = consumer; this.scenarios = scenarios; this.sensors = sensors; this.hubRouter = hubRouter; this.topic = topic;
    }
    public void start() {
        try {
            consumer.subscribe(List.of(topic));
            while (true) {
                var records = consumer.poll(Duration.ofSeconds(1));
                records.forEach(record -> {
                    try {
                        process(record.value());
                    } catch (Exception e) {
                        // A malformed message or a transient gRPC failure must not stop the consumer.
                        log.error("Could not process snapshot for hub {}", record.value().getHubId(), e);
                    }
                });
                if (!records.isEmpty()) consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("Snapshot processor is stopping");
        } catch (Exception e) {
            log.error("Error processing snapshots", e);
        } finally { consumer.close(); }
    }
    public void process(SensorsSnapshotAvro snapshot) {
        if (snapshot == null) return;
        List<Scenario> matching = scenarios.findByHubId(snapshot.getHubId()).stream()
                .filter(scenario -> scenario.getConditions().stream().allMatch(c -> matches(snapshot, c)))
                .toList();
        for (Scenario scenario : matching) {
            scenario.getActions().forEach(action -> send(snapshot.getHubId(), scenario.getName(), action, snapshot.getTimestamp()));
        }
    }
    private boolean matches(SensorsSnapshotAvro snapshot, ScenarioCondition link) {
        Condition condition = link.getCondition();
        SensorStateAvro state = snapshot.getSensorsState().get(link.getSensor().getId());
        if (state == null) return false;
        Object actual = value(state.getData(), condition.getType());
        if (actual == null) return false;
        if (actual instanceof Boolean bool) return condition.getOperation() == ConditionOperation.EQUALS && bool == (condition.getValue() == 1);
        int left = (Integer) actual;
        int right;
        right = condition.getValue();
        return switch (condition.getOperation()) {
            case EQUALS -> left == right;
            case GREATER_THAN -> left > right;
            case LOWER_THAN -> left < right;
        };
    }
    private Object value(Object data, ConditionType type) {
        return switch (type) {
            case MOTION -> data instanceof MotionSensorAvro x ? x.getMotion() : null;
            case LUMINOSITY -> data instanceof LightSensorAvro x ? x.getLuminosity() : null;
            case SWITCH -> data instanceof SwitchSensorAvro x ? x.getState() : null;
            case TEMPERATURE -> data instanceof TemperatureSensorAvro x ? x.getTemperatureC() : data instanceof ClimateSensorAvro x ? x.getTemperatureC() : null;
            case CO2LEVEL -> data instanceof ClimateSensorAvro x ? x.getCo2Level() : null;
            case HUMIDITY -> data instanceof ClimateSensorAvro x ? x.getHumidity() : null;
        };
    }
    private void send(String hubId, String scenarioName, ScenarioAction link, java.time.Instant eventTime) {
        Action action = link.getAction();
        DeviceActionProto.Builder item = DeviceActionProto.newBuilder().setSensorId(link.getSensor().getId())
                .setType(ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto.valueOf(action.getType().name()));
        if (action.getValue() != null) item.setValue(action.getValue());

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(eventTime.getEpochSecond())
                .setNanos(eventTime.getNano())
                .build();
        hubRouter.handleDeviceAction(DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(item)
                .setTimestamp(timestamp)
                .build());
    }
    @PreDestroy public void stop() { consumer.wakeup(); }
}
