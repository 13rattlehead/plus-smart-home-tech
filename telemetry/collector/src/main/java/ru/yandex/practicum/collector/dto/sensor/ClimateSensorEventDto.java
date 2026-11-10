package ru.yandex.practicum.collector.dto.sensor;

import lombok.*;
import ru.yandex.practicum.collector.dto.enums.SensorEventType;

@Getter
@Setter
@ToString
public class ClimateSensorEventDto extends SensorEventDto {

    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

}
