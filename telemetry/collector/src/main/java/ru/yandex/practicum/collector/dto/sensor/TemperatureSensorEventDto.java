package ru.yandex.practicum.collector.dto.sensor;

import lombok.*;
import ru.yandex.practicum.collector.dto.enums.SensorEventType;

@Getter
@Setter
@ToString
public class TemperatureSensorEventDto extends SensorEventDto {

    private Integer temperatureC;
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

}
