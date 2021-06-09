package br.com.concrete.orchestrator.topology.configuration;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

@Named
public class SerdeConfiguration {

    private final String schemaRegistryUrl;

    public SerdeConfiguration(
        @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl
    ) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    public <T extends SpecificRecord> SpecificAvroSerde<T> configure() {
        SpecificAvroSerde<T> specificAvroSerde = new SpecificAvroSerde<>();
        specificAvroSerde.configure(getSerdeConfiguration(), false);

        return specificAvroSerde;
    }

    private Map<String, String> getSerdeConfiguration() {
        final HashMap<String, String> map = new HashMap<>();
        map.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        return map;
    }
}
