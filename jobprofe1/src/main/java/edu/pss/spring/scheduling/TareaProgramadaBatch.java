package edu.pss.spring.scheduling;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.pss.spring.service.ProcesadorBatch;

@Component
public class TareaProgramadaBatch {
	
	private final ProcesadorBatch procesador;

    public TareaProgramadaBatch(ProcesadorBatch procesador) {
        this.procesador = procesador;
    }
    
    //https://docs.spring.io/spring-framework/reference/integration/scheduling.html

   // @Scheduled(initialDelay = 10000, fixedDelay = 30000)
    @Scheduled (
    		cron = "${batch.scheduling.cron}",
    		zone = "Europe/Madrid"
    		)
    public void ejecutarJobProgramado() {

        System.out.println("===== EJECUCIÓN PROGRAMADA =====");

        try {
            JobExecution ejecucion = procesador.ejecutar();

            System.out.println("ID ejecución: " + ejecucion.getId());
            System.out.println("Estado: " + ejecucion.getStatus());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

   
}
