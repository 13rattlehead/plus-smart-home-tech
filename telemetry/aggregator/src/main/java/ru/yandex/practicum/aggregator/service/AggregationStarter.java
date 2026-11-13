package ru.yandex.practicum.aggregator.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Consumes sensor measurements and publishes a new snapshot only when a hub state changes. */
@Slf4j
@Component
public class AggregationStarter {
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(1);

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final String sensorsTopic;
    private final String snapshotsTopic;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public AggregationStarter(KafkaConsumer<String, SensorEventAvro> consumer,
                              KafkaProducer<String, SpecificRecordBase> producer,
                              @Value("${aggregator.kafka.topics.sensors}") String sensorsTopic,
                              @Value("${aggregator.kafka.topics.snapshots}") String snapshotsTopic) {
        this.consumer = consumer;
        this.producer = producer;
        this.sensorsTopic = sensorsTopic;
        this.snapshotsTopic = snapshotsTopic;
    }

    public void start() {
        try {
            consumer.subscribe(java.util.List.of(sensorsTopic));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(POLL_TIMEOUT);
                boolean hasUpdates = false;
                for (var record : records) {
                    Optional<SensorsSnapshotAvro> snapshot = updateState(record.value());
                    if (snapshot.isPresent()) {
                        SensorsSnapshotAvro value = snapshot.get();
                        producer.send(new ProducerRecord<>(snapshotsTopic, value.getHubId(), value));
                        hasUpdates = true;
                    }
                }
                if (!records.isEmpty()) {
                    // Do not acknowledge input until all snapshots from this batch are durably sent.
                    if (hasUpdates) producer.flush();
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
            log.info("Aggregation is stopping");
        } catch (Exception e) {
            log.error("Error while processing sensor events", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                log.warn("Could not finish Kafka processing cleanly", e);
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }

    /**
     * Updates the in-memory snapshot for the event's hub. Older and duplicate events leave it unchanged.
     */
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        if (event == null) return Optional.empty();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), hubId ->
                new SensorsSnapshotAvro(hubId, event.getTimestamp(), new HashMap<>()));
        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

        if (oldState != null && (oldState.getTimestamp().isAfter(event.getTimestamp())
                || oldState.getData().equals(event.getPayload()))) {
            return Optional.empty();
        }

        SensorStateAvro newState = new SensorStateAvro(event.getTimestamp(), event.getPayload());
        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        return Optional.of(snapshot);
    }

    @PreDestroy
    public void stop() {
        consumer.wakeup();
    }
}
