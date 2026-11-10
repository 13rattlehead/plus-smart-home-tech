package telemetry.collector;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.grpc.telemetry.event.CollectorControllerGrpc.CollectorControllerImplBase;
import telemetry.collector.handler.hub.HubEventHandler;
import telemetry.collector.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class EventController extends CollectorControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;

    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorEventHandlers,
                           Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType,
                        Function.identity()
                ));
    }

    @Override
    public void collectSensorEvent(
            SensorEventProto request,
            StreamObserver<Empty> responseObserver
    ) {
        try {
            SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();
            SensorEventHandler handler = sensorEventHandlers.get(payloadCase);

            if (handler == null) {
                throw new IllegalArgumentException(
                        "Не найден обработчик для датчика: " + payloadCase);
            }

            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при обработке события датчика", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void collectHubEvent(
            HubEventProto request,
            StreamObserver<Empty> responseObserver
    ) {
        try {
            HubEventProto.PayloadCase payloadCase = request.getPayloadCase();
            HubEventHandler handler = hubEventHandlers.get(payloadCase);

            if (handler == null) {
                throw new IllegalArgumentException(
                        "Не найден обработчик события для хаба: " + payloadCase
                );
            }

            handler.hanlde(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Ошибка при обработке события хаба");

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
                            .asRuntimeException());
        }
    }




}