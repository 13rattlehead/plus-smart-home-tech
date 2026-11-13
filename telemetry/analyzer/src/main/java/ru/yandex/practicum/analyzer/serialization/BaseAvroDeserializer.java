package ru.yandex.practicum.analyzer.serialization;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.*;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final DecoderFactory decoderFactory;
    private final Schema schema;
    public BaseAvroDeserializer(Schema schema) { this(DecoderFactory.get(), schema); }
    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) { this.decoderFactory = decoderFactory; this.schema = schema; }
    @Override public T deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try { return new SpecificDatumReader<T>(schema).read(null, decoderFactory.binaryDecoder(data, null)); }
        catch (IOException e) { throw new SerializationException("Unable to deserialize Avro from " + topic, e); }
    }
}
