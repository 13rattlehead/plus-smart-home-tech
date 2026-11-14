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
    private final ConditionRepository conditions;
    private final ActionRepository actions;
    private final String topic;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer, SensorRepository sensors,
                             ScenarioRepository scenarios, ConditionRepository conditions, ActionRepository actions,
                             @Value("${analyzer.kafka.topics.hubs}") String topic) {
        this.consumer = consumer; this.sensors = sensors; this.scenarios = scenarios;
        this.conditions = conditions; this.actions = actions; this.topic = topic;
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
                    .orElseGet(() -> sensors.save(new Sensor(added.getId(), event.getHubId())));
        } else if (payload instanceof DeviceRemovedEventAvro removed) {
            sensors.findByIdAndHubId(removed.getId(), event.getHubId()).ifPresent(sensors::delete);
        } else if (payload instanceof ScenarioAddedEventAvro added) {
            Scenario scenario = scenarios.findByHubIdAndName(event.getHubId(), added.getName())
                    .orElseGet(() -> new Scenario(event.getHubId(), added.getName()));
            scenario.getConditions().clear();
            scenario.getActions().clear();
            scenario = scenarios.saveAndFlush(scenario);
            Scenario finalScenario = scenario;
            added.getConditions().forEach(value -> sensors.findByIdAndHubId(value.getSensorId(), event.getHubId()).ifPresent(sensor -> {
                Condition condition = conditions.save(condition(value));
                finalScenario.getConditions().add(new ScenarioCondition(finalScenario, sensor, condition));
            }));
            added.getActions().forEach(value -> sensors.findByIdAndHubId(value.getSensorId(), event.getHubId()).ifPresent(sensor -> {
                Action action = actions.save(action(value));
                finalScenario.getActions().add(new ScenarioAction(finalScenario, sensor, action));
            }));
            scenarios.save(finalScenario);
        } else if (payload instanceof ScenarioRemovedEventAvro removed) {
            scenarios.findByHubIdAndName(event.getHubId(), removed.getName()).ifPresent(scenarios::delete);
        }
    }

    private Condition condition(ScenarioConditionAvro value) {
        Object rawValue = value.getValue();
        int conditionValue = rawValue instanceof Boolean bool ? (bool ? 1 : 0) : (Integer) rawValue;
        return new Condition(ConditionType.valueOf(value.getType().name()),
                ConditionOperation.valueOf(value.getOperation().name()), conditionValue);
    }
    private Action action(DeviceActionAvro value) {
        return new Action(ActionType.valueOf(value.getType().name()), (Integer) value.getValue());
    }
    @PreDestroy public void stop() { consumer.wakeup(); }
}
