package com.myfund.advice.observation;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Micrometer 观测配置，后续可扩展 Prometheus/ARMS exporter。
 */
@Configuration
public class ObservationConfig {

    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }
}
