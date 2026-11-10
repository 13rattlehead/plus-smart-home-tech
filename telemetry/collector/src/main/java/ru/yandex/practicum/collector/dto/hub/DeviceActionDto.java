package ru.yandex.practicum.collector.dto.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.dto.enums.ActionType;


@Getter
@Setter
@ToString
public class DeviceActionDto {

    @NotBlank
    private String sensorId;

    @NotNull
    private ActionType type;

    private Integer value;
}
