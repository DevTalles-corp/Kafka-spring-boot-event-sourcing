package com.bistro.reservations.outbox;

import com.bistro.reservations.events.ReservationCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JsonMapper jsonMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPending(){

        List<OutboxMessage> pending = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for( OutboxMessage message : pending ){
            ReservationCreated event = jsonMapper.readValue(message.getPayload(), ReservationCreated.class);

            kafkaTemplate.send(message.getTopic(), message.getMessageKey(), event);
//            kafkaTemplate.send(message.getTopic(), message.getMessageKey(), event);

            message.setStatus(OutboxStatus.SENT);

            log.info("Outbox: mensaje {} publicado a {} y marcado SENT",
                    message.getId(), message.getTopic());
        }
    }

}





















