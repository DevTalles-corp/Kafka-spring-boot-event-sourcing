package com.bistro.notifications.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dlt")
@RequiredArgsConstructor
public class DltReprocessingController {

    private final KafkaListenerEndpointRegistry registry;

    @PostMapping("/reprocess")
    public ResponseEntity<String> reprocess(){

        MessageListenerContainer container = registry.getListenerContainer("confirmedDltReprocessor");

        if(container==null){
            return ResponseEntity.badRequest().body("No existe el reprocesador");
        }
        if(container.isRunning()){
            return ResponseEntity.ok("El reprocesador ya estaba corriendo");
        }

        container.start();
        return ResponseEntity.ok("Reprocesador iniciado: en reservation-confirmed-dlt");
    }

}











