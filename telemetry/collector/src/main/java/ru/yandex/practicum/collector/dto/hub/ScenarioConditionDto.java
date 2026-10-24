package ru.yandex.practicum.collector.dto.hub;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.dto.enums.ConditionOperation;
import ru.yandex.practicum.collector.dto.enums.ConditionType;

@Getter
@Setter
@ToString
public class ScenarioConditionDto {

    @NotBlank
    private String sensorId;

    @NotNull
    private ConditionType type;

    @NotNull
    private ConditionOperation operation;

    private Object value;

    @AssertTrue(message = "value должен быть null, целым числом или boolean")
    public boolean isValueSupported() {
        return value == null
                || value instanceof Integer
                || value instanceof Boolean;
    }
}
