package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "scenarios", uniqueConstraints = @UniqueConstraint(columnNames = {"hubId", "name"}))
@Getter @Setter @NoArgsConstructor
public class Scenario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String hubId;
    @Column(nullable = false) private String name;
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ScenarioCondition> conditions = new LinkedHashSet<>();
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ScenarioAction> actions = new LinkedHashSet<>();
    public Scenario(String hubId, String name) { this.hubId = hubId; this.name = name; }
}
