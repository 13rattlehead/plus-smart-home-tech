package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_conditions")
@Getter @Setter @NoArgsConstructor
public class Condition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String sensorId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ConditionType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ConditionOperation operation;
    @Column(nullable = false) private String value;
    public Condition(String sensorId, ConditionType type, ConditionOperation operation, String value) {
        this.sensorId = sensorId; this.type = type; this.operation = operation; this.value = value;
    }
}
