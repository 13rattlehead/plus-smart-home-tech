package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conditions")
@Getter @Setter @NoArgsConstructor
public class Condition {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ConditionType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ConditionOperation operation;
    @Column(nullable = false) private Integer value;
    public Condition(ConditionType type, ConditionOperation operation, Integer value) {
        this.type = type; this.operation = operation; this.value = value;
    }
}
