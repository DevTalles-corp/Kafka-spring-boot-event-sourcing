package com.bistro.shared.reprocessing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DltReprocessingService {

    private static final String REPROCESSOR_ID = "dltReprocessor";

    private final KafkaListenerEndpointRegistry registry;

    public void start(){
        MessageListenerContainer container = registry.getListenerContainer(REPROCESSOR_ID);

        if(container != null && !container.isRunning()){
            container.start();
            log.info("Reprocesador de DLT encendido");
        }
    }

    public void stop() {
        MessageListenerContainer container = registry.getListenerContainer(REPROCESSOR_ID);
        if (container != null && container.isRunning()) {
            container.stop();
            log.info("Reprocesador de DLT apagado");
        }
    }
}













