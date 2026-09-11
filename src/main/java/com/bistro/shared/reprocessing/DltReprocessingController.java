package com.bistro.shared.reprocessing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dlt")
@RequiredArgsConstructor
public class DltReprocessingController {

    private final DltReprocessingService reprocessingService;

    @PostMapping("/reprocess")
    public ResponseEntity<String> reprocess(){
        reprocessingService.start();
        return ResponseEntity.ok("Reprocesador iniciado: drenando los -dlt");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stop(){
        reprocessingService.stop();
        return ResponseEntity.ok("Reprocesador detenido");
    }
}
