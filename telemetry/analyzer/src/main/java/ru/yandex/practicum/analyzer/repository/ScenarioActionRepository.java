package ru.yandex.practicum.analyzer.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.entity.*;
public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> { }
