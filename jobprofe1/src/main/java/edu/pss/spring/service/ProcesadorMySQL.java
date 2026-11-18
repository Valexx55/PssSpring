package edu.pss.spring.service;

import java.util.UUID;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


@Service("procesadorMySQL")
@ConditionalOnProperty(
	    name = "batch.procesador",
	    havingValue = "MYSQL"
	)
public class ProcesadorMySQL implements ProcesadorBatch {

    private final JobOperator jobOperator;
    private final Job job;

    // Inyección de dependencias por constructor
    public ProcesadorMySQL(
            JobOperator jobOperator,
            @Qualifier("jobGestionClientesSQL") Job job) {

        this.jobOperator = jobOperator;
        this.job = job;
    }

    @Override
    public JobExecution ejecutar() throws Exception {

        JobParameters parametros = new JobParametersBuilder()
                .addString("idSolicitud", UUID.randomUUID().toString())
                .toJobParameters();

        return jobOperator.start(job, parametros);
    }
}
