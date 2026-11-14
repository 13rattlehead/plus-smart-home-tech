package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter @Setter @NoArgsConstructor
public class ScenarioAction {
    @EmbeddedId private ScenarioActionId id;
    @ManyToOne(fetch = FetchType.LAZY) @MapsId("scenarioId") @JoinColumn(name = "scenario_id") private Scenario scenario;
    @ManyToOne(fetch = FetchType.EAGER) @MapsId("sensorId") @JoinColumn(name = "sensor_id") private Sensor sensor;
    @ManyToOne(fetch = FetchType.EAGER) @MapsId("actionId") @JoinColumn(name = "action_id") private Action action;
    public ScenarioAction(Scenario scenario, Sensor sensor, Action action) {
        this.scenario = scenario; this.sensor = sensor; this.action = action;
        this.id = new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId());
    }
}
