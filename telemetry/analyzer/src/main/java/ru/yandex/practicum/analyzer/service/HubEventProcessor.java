package ru.yandex.practicum.analyzer.service;

import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.entity.*;
import ru.yandex.practicum.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final SensorRepository sensors;
    private final ScenarioRepository scenarios;
    private final String topic;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer, SensorRepository sensors,
                             ScenarioRepository scenarios, @Value("${analyzer.kafka.topics.hubs}") String topic) {
        this.consumer = consumer; this.sensors = sensors; this.scenarios = scenarios; this.topic = topic;
    }

    @Override public void run() {
        try {
            consumer.subscribe(List.of(topic));
            while (true) consumer.poll(Duration.ofSeconds(1)).forEach(record -> process(record.value()));
        } catch (WakeupException ignored) {
            log.info("Hub events processor is stopping");
        } catch (Exception e) {
            log.error("Error processing hub events", e);
        } finally { consumer.close(); }
    }

    @Transactional
    public void process(HubEventAvro event) {
        if (event == null) return;
        Object payload = event.getPayload();
        if (payload instanceof DeviceAddedEventAvro added) {
            sensors.findByIdAndHubId(added.getId(), event.getHubId())
                    .orElseGet(() -> sensors.save(new Sensor(added.getId(), event.getHubId(), DeviceType.valueOf(added.getType().name()))));
        } else if (payload instanceof DeviceRemovedEventAvro removed) {
            sensors.findByIdAndHubId(removed.getId(), event.getHubId()).ifPresent(sensors::delete);
        } else if (payload instanceof ScenarioAddedEventAvro added) {
            Scenario scenario = scenarios.findByHubIdAndName(event.getHubId(), added.getName())
                    .orElseGet(() -> new Scenario(event.getHubId(), added.getName()));
            scenario.getConditions().clear();
            scenario.getConditions().addAll(added.getConditions().stream().map(this::condition).toList());
            scenario.getActions().clear();
            scenario.getActions().addAll(added.getActions().stream().map(this::action).toList());
            scenarios.save(scenario);
        } else if (payload instanceof ScenarioRemovedEventAvro removed) {
            scenarios.findByHubIdAndName(event.getHubId(), removed.getName()).ifPresent(scenarios::delete);
        }
    }

    private Condition condition(ScenarioConditionAvro value) {
        return new Condition(value.getSensorId(), ConditionType.valueOf(value.getType().name()),
                ConditionOperation.valueOf(value.getOperation().name()), String.valueOf(value.getValue()));
    }
    private Action action(DeviceActionAvro value) {
        return new Action(value.getSensorId(), ActionType.valueOf(value.getType().name()), (Integer) value.getValue());
    }
    @PreDestroy public void stop() { consumer.wakeup(); }
}
