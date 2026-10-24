package ru.yandex.practicum.collector.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.collector.serialization.AvroSerializer;
import org.apache.avro.specific.SpecificRecordBase;

import java.util.Properties;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Properties properties = new Properties();

        properties.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );
        properties.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName()
        );
        properties.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                AvroSerializer.class.getName()
        );
        properties.put(ProducerConfig.ACKS_CONFIG, "all");

        return new KafkaProducer<>(properties);
    }
}