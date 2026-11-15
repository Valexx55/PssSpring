
package edu.pss.spring.controller;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pss.spring.service.BatchService;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final BatchService batchService;

    // Inyección por constructor
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/ejecutar")
    public ResultadoBatch ejecutar() throws Exception {

        JobExecution ejecucion = batchService.ejecutarFichero();

        return new ResultadoBatch(
                ejecucion.getId(),
                ejecucion.getStatus().toString()
        );
    }

    public record ResultadoBatch(
            Long idEjecucion,
            String estado
    ) {}
}
