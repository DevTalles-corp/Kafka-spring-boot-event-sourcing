package com.bistro.shared.reprocessing;

import com.bistro.reservations.events.ReservationConfirmed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DltReprocessor {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            id = "dltReprocessor",
            topicPattern = ".*-dlt",
            groupId = "dlt-reprocessor",
            autoStartup = "false",
            properties = { "auto.offset.reset=earliest" }
    )
    public void reprocess(ConsumerRecord<String, Object> record){

        String topic = record.topic().replace("-dlt", "");

        log.info("Reprocesando desde {} → {} (key={})",
                record.topic(), topic, record.key());

        kafkaTemplate.send(topic, record.key(), record.value());

    }
}











