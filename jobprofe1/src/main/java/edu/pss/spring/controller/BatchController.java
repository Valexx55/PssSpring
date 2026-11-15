
package edu.pss.spring.controller;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.pss.spring.service.BatchService;
import edu.pss.spring.service.ProcesadorBatch;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private final BatchService batchService;
    private final ProcesadorBatch procesador;

    // Inyección por constructor
    public BatchController(BatchService batchService, 
 //   		@Qualifier("procesadorCSV") ProcesadorBatch procesadorBatch ) {
    	//@Qualifier("procesadorMySQL") ProcesadorBatch procesadorBatch ) {
    	 ProcesadorBatch procesadorBatch ) {
        this.batchService = batchService;
        this.procesador = procesadorBatch;
    }
    
    @PostMapping("/ejecutar-sql")
    public ResultadoBatch ejecutarSQL() throws Exception {

        JobExecution ejecucion = batchService.ejecutarSQL();

        return new ResultadoBatch(
                ejecucion.getId(),
                ejecucion.getStatus().toString()
        );
    }

    @PostMapping("/ejecutar-csv")
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

    @PostMapping("/ejecutar-di")
    public ResultadoBatch ejecutarConInyeccion() throws Exception {

        JobExecution ejecucion = procesador.ejecutar();

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
