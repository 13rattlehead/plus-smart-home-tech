package ru.yandex.practicum.collector.dto.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.collector.dto.enums.DeviceType;
import ru.yandex.practicum.collector.dto.enums.HubEventType;

@Getter
@Setter
@ToString
public class DeviceAddedEventDto extends HubEventDto {

    @NotBlank
    private String id;

    @NotNull
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
