package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import java.util.*;

@Entity
@Table(name = "scenarios", uniqueConstraints = @UniqueConstraint(columnNames = {"hubId", "name"}))
@Getter @Setter @NoArgsConstructor
public class Scenario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String hubId;
    @Column(nullable = false) private String name;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "scenario_id")
    @Fetch(FetchMode.SUBSELECT)
    private List<Condition> conditions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "scenario_id")
    @Fetch(FetchMode.SUBSELECT)
    private List<Action> actions = new ArrayList<>();
    public Scenario(String hubId, String name) { this.hubId = hubId; this.name = name; }
}
