
package edu.pss.spring.service;

import java.util.UUID;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    private final JobOperator jobOperator;
    private final Job jobGestionFichero;

    // Inyección de dependencias por constructor
    public BatchService(
            JobOperator jobOperator,
            @Qualifier("jobGestionFichero") Job jobGestionFichero) {

        this.jobOperator = jobOperator;
        this.jobGestionFichero = jobGestionFichero;
    }

    public JobExecution ejecutarFichero() throws Exception {

        // Identificador único para cada lanzamiento
        JobParameters parametros = new JobParametersBuilder()
                .addString("idSolicitud", UUID.randomUUID().toString())
                .toJobParameters();

        // Lanzamos nuestro Job
        return jobOperator.start(jobGestionFichero, parametros);
    }
}
