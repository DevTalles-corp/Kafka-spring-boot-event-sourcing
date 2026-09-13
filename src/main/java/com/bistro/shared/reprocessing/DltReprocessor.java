package com.bistro.shared.reprocessing;

import com.bistro.reservations.events.ReservationConfirmed;
import com.bistro.shared.NonRetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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

        if(isPermanent(record)){
            log.warn("Salteado (permanente, no reintentable): topic={}, key={}",
                    record.topic(), record.key());
            return;
        }

        String topic = record.topic().replace("-dlt", "");

        log.info("Reprocesando desde {} → {} (key={})",
                record.topic(), topic, record.key());

        kafkaTemplate.send(topic, record.key(), record.value());
    }

    private boolean isPermanent( ConsumerRecord<String, Object> record ){
        Header cause = record.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_CAUSE_FQCN);

        if(cause==null){
            return false;
        }
        //Fully Qualified Class Name
        String fqcn = new String(cause.value(), StandardCharsets.UTF_8);

        return NonRetryableException.class.getName().equals(fqcn);
    }
}











