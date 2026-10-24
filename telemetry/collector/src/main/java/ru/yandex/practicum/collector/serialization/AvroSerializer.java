
package ru.yandex.practicum.collector.serialization;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroSerializer implements Serializer<SpecificRecordBase> {

    @Override
    public byte[] serialize(String topic, SpecificRecordBase event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            DatumWriter<SpecificRecordBase> writer =
                    new SpecificDatumWriter<>(event.getSchema());

            BinaryEncoder encoder =
                    EncoderFactory.get().binaryEncoder(output, null);

            writer.write(event, encoder);
            encoder.flush();

            return output.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(
                    "Ошибка сериализации события для топика [" + topic + "]",
                    e
            );
        }
    }
}
