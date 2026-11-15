
package edu.pss.spring.controller;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @PostMapping("/ejecutar-sql")
    public ResultadoBatch ejecutarSQL() throws Exception {

        JobExecution ejecucion = batchService.ejecutarSQL();

        return new ResultadoBatch(
                ejecucion.getId(),
                ejecucion.getStatus().toString()
        );
    }

    @PostMapping("/ejecutar")
    public ResultadoBatch ejecutar() throws Exception {

        JobExecution ejecucion = batchService.ejecutarFichero();

        return new ResultadoBatch(
                ejecucion.getId(),
                ejecucion.getStatus().toString()
        );
    }
    
    @GetMapping("/ejecuciones/{id}")
    public ResponseEntity<ResultadoBatch> consultar(
            @PathVariable Long id) {

        JobExecution ejecucion =
                batchService.consultarEjecucion(id);

        if (ejecucion == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new ResultadoBatch(
                        ejecucion.getId(),
                        ejecucion.getStatus().toString()
                )
        );
    }

    public record ResultadoBatch(
            Long idEjecucion,
            String estado
    ) {}
}
