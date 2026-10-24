package ru.yandex.practicum.collector.dto.sensor;

import lombok.*;
import ru.yandex.practicum.collector.dto.enums.SensorEventType;

@Getter
@Setter
@ToString
public class LightSensorEventDto extends SensorEventDto {

    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

}
