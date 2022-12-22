package com.example.utils.kafka;

import com.example.web.dto.AuthKeyInfoDTO;
import com.example.web.dto.MemberAuthInfoDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource(value = "application-dev.yml")
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = getCustomConfigProps(StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, MemberAuthInfoDTO> memberAuthInfoDTOProducerFactory() {
        Map<String, Object> configProps = getCustomConfigProps(JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MemberAuthInfoDTO> memberInfoDTOKafkaTemplate() {
        return new KafkaTemplate<>(memberAuthInfoDTOProducerFactory());
    }

    @Bean
    public ProducerFactory<String, AuthKeyInfoDTO> authKeyInfoDTOProducerFactory() {
        Map<String, Object> configProps = getCustomConfigProps(JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AuthKeyInfoDTO> authKeyInfoDTOKafkaTemplate() {
        return new KafkaTemplate<>(authKeyInfoDTOProducerFactory());
    }

    private <T> Map<String, Object> getCustomConfigProps( T valueSerializer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                valueSerializer);
        return configProps;
    }
}
