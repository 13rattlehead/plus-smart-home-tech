package telemetry.collector.handler.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

public class TemperatureSensorEventHandler implements SensorEventHandler {

    private static final Logger log =
            LoggerFactory.getLogger(TemperatureSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        TemperatureSensorProto sensor = event.getTemperatureSensor();

        log.info("Получено сообщение от датчика температуры: {}", sensor);
    }
}
