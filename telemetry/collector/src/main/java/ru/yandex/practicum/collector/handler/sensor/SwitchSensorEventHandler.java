package telemetry.collector.handler.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

public class SwitchSensorEventHandler implements SensorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(SwitchSensorEventHandler.class);

    @Override
    public void handle(SensorEventProto event) {
        SwitchSensorProto sensor = event.getSwitchSensor();

        log.info("Получено сообщения от свитч-датчика: {}", sensor);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }
}
