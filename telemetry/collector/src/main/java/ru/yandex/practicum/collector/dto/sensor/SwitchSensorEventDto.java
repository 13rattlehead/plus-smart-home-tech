package ru.yandex.practicum.collector.dto.sensor;

import lombok.*;
import ru.yandex.practicum.collector.dto.enums.SensorEventType;

@Getter
@Setter
@ToString
public class SwitchSensorEventDto extends SensorEventDto {

    private Boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

}
