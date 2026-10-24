package ru.yandex.practicum.collector.dto.hub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.dto.enums.HubEventType;

import java.util.List;


@Getter
@Setter
@ToString
public class ScenarioAddedEventDto extends HubEventDto {

    @NotBlank
    private String name;

    @NotEmpty
    @Valid
    private List<ScenarioConditionDto> conditions;

    @NotEmpty
    @Valid
    private List<DeviceActionDto> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
