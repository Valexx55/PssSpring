
package edu.pss.spring.service;

import java.util.UUID;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    private final JobOperator jobOperator;
    private final Job jobGestionFichero;
    private final JobRepository jobRepository;
    private final Job jobGestionClientesSQL;

    // Inyección de dependencias por constructor
    public BatchService(
            JobOperator jobOperator,
            @Qualifier("jobGestionFichero") Job jobGestionFichero,
            JobRepository jobRepository,
            @Qualifier("jobGestionClientesSQL") Job jobGestionClientesSQL) {

        this.jobOperator = jobOperator;
        this.jobGestionFichero = jobGestionFichero;
        this.jobRepository = jobRepository;
        this.jobGestionClientesSQL = jobGestionClientesSQL;
    }

    public JobExecution ejecutarFichero() throws Exception {

        // Identificador único para cada lanzamiento
        JobParameters parametros = new JobParametersBuilder()
                .addString("idSolicitud", UUID.randomUUID().toString())
                .toJobParameters();

        // Lanzamos nuestro Job
        return jobOperator.start(jobGestionFichero, parametros);
    }
    
    public JobExecution consultarEjecucion(Long id) {
        return jobRepository.getJobExecution(id);
    }
    
    public JobExecution ejecutarSQL() throws Exception {

        JobParameters parametros = new JobParametersBuilder()
                .addString("idSolicitud", UUID.randomUUID().toString())
                .toJobParameters();

        return jobOperator.start(jobGestionClientesSQL, parametros);
    }
    

public JobExecution reiniciarEjecucion(Long idEjecucion) throws Exception {

    // Recuperamos la ejecución original de la base de datos
    JobExecution ejecucionAnterior =
            jobRepository.getJobExecution(idEjecucion);

    if (ejecucionAnterior == null) {
        throw new IllegalArgumentException(
                "No existe la ejecución: " + idEjecucion
        );
    }

    // Solo permitimos reiniciar una ejecución fallida
    if (ejecucionAnterior.getStatus() != BatchStatus.FAILED) {
        throw new IllegalStateException(
                "La ejecución no está en estado FAILED"
        );
    }

    // Reiniciamos la misma instancia del Job
    return jobOperator.restart(ejecucionAnterior);
}

}
