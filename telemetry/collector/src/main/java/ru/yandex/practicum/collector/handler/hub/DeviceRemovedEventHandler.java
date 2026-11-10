package ru.yandex.practicum.collector.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class DeviceRemovedEventHandler implements HubEventHandler {

    private static final Logger log = LoggerFactory.getLogger(DeviceRemovedEventHandler.class);

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void hanlde(HubEventProto event) {
        DeviceRemovedEventProto device = event.getDeviceRemoved();
        log.info("Удалено устройство: {}", device);
    }
}
