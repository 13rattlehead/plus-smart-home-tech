package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_conditions")
@Getter @Setter @NoArgsConstructor
public class ScenarioCondition {
    @EmbeddedId private ScenarioConditionId id;
    @ManyToOne(fetch = FetchType.LAZY) @MapsId("scenarioId") @JoinColumn(name = "scenario_id") private Scenario scenario;
    @ManyToOne(fetch = FetchType.EAGER) @MapsId("sensorId") @JoinColumn(name = "sensor_id") private Sensor sensor;
    @ManyToOne(fetch = FetchType.EAGER) @MapsId("conditionId") @JoinColumn(name = "condition_id") private Condition condition;
    public ScenarioCondition(Scenario scenario, Sensor sensor, Condition condition) {
        this.scenario = scenario; this.sensor = sensor; this.condition = condition;
        this.id = new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId());
    }
}
