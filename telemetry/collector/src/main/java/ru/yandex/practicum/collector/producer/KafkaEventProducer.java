package ru.yandex.practicum.collector.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class KafkaEventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final String sensorsTopic;
    private final String hubsTopic;

    public KafkaEventProducer(
            KafkaProducer<String, SpecificRecordBase> producer,
            @Value("${collector.kafka.topics.sensors}") String sensorsTopic,
            @Value("${collector.kafka.topics.hubs}") String hubsTopic) {
        this.producer = producer;
        this.sensorsTopic = sensorsTopic;
        this.hubsTopic = hubsTopic;
    }

    public void sendSensorEvent(SensorEventAvro event) {
        send(sensorsTopic, event);
    }

    public void sendHubEvent(HubEventAvro event) {
        send(hubsTopic, event);
    }

    private void send(String topic, SpecificRecordBase event) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, event);
        producer.send(record);
    }
}
