package telemetry.collector.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public class DeviceAddedEventHandler implements HubEventHandler {

    private static final Logger log = LoggerFactory.getLogger(DeviceAddedEventHandler.class);

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void hanlde(HubEventProto event) {
        DeviceAddedEventProto device = event.getDeviceAdded();
        log.info("Добавлено новое устройство: {}", device);
    }
}
