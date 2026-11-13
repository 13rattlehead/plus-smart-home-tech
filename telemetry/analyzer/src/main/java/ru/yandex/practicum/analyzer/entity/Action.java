package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter @Setter @NoArgsConstructor
public class Action {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String sensorId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ActionType type;
    private Integer value;
    public Action(String sensorId, ActionType type, Integer value) {
        this.sensorId = sensorId; this.type = type; this.value = value;
    }
}
