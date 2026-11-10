package telemetry.collector.handler.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public class LightSensorEventHandler implements SensorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(LightSensorEventHandler.class);

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorProto sensor = event.getLightSensor();

        log.info("Получено сообщение от датчка света: {}", sensor);
    }
}
