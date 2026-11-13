package ru.yandex.practicum.analyzer.config;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import ru.yandex.practicum.analyzer.serialization.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Bean
    KafkaConsumer<String, HubEventAvro> hubConsumer(@Value("${spring.kafka.bootstrap-servers}") String brokers,
                                                    @Value("${analyzer.kafka.groups.hubs}") String group) {
        return new KafkaConsumer<>(properties(brokers, group, HubEventDeserializer.class, true));
    }
    @Bean
    KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer(@Value("${spring.kafka.bootstrap-servers}") String brokers,
                                                                 @Value("${analyzer.kafka.groups.snapshots}") String group) {
        return new KafkaConsumer<>(properties(brokers, group, SensorsSnapshotDeserializer.class, false));
    }
    private Properties properties(String brokers, String group, Class<?> valueDeserializer, boolean autoCommit) {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getName());
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return p;
    }
}
