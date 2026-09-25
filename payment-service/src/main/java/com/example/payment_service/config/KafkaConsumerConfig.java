package com.example.payment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {

        // Wait 2 seconds between retries
        // Retry 2 times after the first attempt
        FixedBackOff fixedBackOff = new FixedBackOff(2000L, 2);

        return new DefaultErrorHandler(fixedBackOff);
    }
}