package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter @Setter @NoArgsConstructor
public class Action {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private ActionType type;
    private Integer value;
    public Action(ActionType type, Integer value) {
        this.type = type; this.value = value;
    }
}
