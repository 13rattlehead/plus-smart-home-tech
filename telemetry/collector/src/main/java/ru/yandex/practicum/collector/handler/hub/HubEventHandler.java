package telemetry.collector.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {

    HubEventProto.PayloadCase getMessageType();

    void hanlde(HubEventProto event);
}
