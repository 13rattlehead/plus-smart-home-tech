package ru.yandex.practicum.collector.dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.yandex.practicum.collector.dto.enums.HubEventType;

@Getter
@Setter
@ToString
public class DeviceRemovedEventDto extends HubEventDto {

    @NotBlank
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
