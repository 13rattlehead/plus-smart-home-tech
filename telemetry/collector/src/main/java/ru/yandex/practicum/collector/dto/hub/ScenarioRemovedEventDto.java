package ru.yandex.practicum.collector.dto.hub;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.collector.dto.enums.HubEventType;


@Getter
@Setter
@ToString
public class ScenarioRemovedEventDto extends HubEventDto {

    @NotNull
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
