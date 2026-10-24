package ru.yandex.practicum.collector.dto.sensor;

import lombok.*;
import ru.yandex.practicum.collector.dto.enums.SensorEventType;

@Getter
@Setter
@ToString
public class MotionSensorEventDto extends SensorEventDto {

    private Integer linkQuality;
    private Boolean motion;
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
