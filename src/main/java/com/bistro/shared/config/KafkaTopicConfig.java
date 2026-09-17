package com.bistro.shared.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic reservationCreatedTopic(){
        return TopicBuilder.name("reservation-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tableAssignedTopic(){
        return TopicBuilder.name("table-assigned")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationConfirmedTopic() {
        return TopicBuilder.name("reservation-confirmed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic tableUnavailableTopic() {
        return TopicBuilder.name("table-unavailable")
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic reservationRejectedTopic() {
        return TopicBuilder.name("reservation-rejected")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //"reservation-confirmed-dlt"
    @Bean
    public NewTopic reservationConfirmedDltTopic() {
        return TopicBuilder.name("reservation-confirmed-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationCancelledTopic() {
        return TopicBuilder.name("reservation-cancelled")
                .partitions(3)
                .replicas(1)
                .build();
    }
}














