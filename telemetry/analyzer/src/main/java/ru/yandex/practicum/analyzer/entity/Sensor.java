package ru.yandex.practicum.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Sensor {
    @Id private String id;
    @Column(nullable = false) private String hubId;
    // The supplied schema keeps only the sensor-to-hub relation. Device type is carried by Kafka events.
}
