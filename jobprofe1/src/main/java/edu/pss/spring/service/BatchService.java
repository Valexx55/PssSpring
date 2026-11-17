package edu.pss.spring.service;

import java.util.UUID;

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

	// para lanzar trabajos manualmente
	JobOperator jobOperator;

	Job jobGestionFichero;
	Job jobGestionClientesSQL;
	// para leer del histórico de trabajos
	JobRepository jobRepository;

	// joboperator nos permite laznar trabajos progrmáticamente
	public BatchService(JobOperator jobOperator, @Qualifier("jobGestionFichero") Job jobGestionFichero,
			JobRepository jobRepository, Job jobGestionClientesSQL) {
		// jobRepository.getJobExecution(55);

		this.jobOperator = jobOperator;
		this.jobGestionFichero = jobGestionFichero;
		this.jobRepository = jobRepository;
		this.jobGestionClientesSQL = jobGestionClientesSQL;
	}

	// propagamos la execpción
	public JobExecution ejecutarFicheroBatchCSV() throws Exception {

		JobParameters parametros = new JobParametersBuilder().addString("idSolicitud", UUID.randomUUID().toString())
				.toJobParameters();
		return this.jobOperator.start(this.jobGestionFichero, parametros);
	}

	public JobExecution ejecucionConsulta(long idEjecucion) throws Exception {
		return jobRepository.getJobExecution(idEjecucion);
	}

	// propagamos la execpción
	public JobExecution ejecutarJobBatchMySQL() throws Exception {

		JobParameters parametros = new JobParametersBuilder()
				.addString("idSolicitud", UUID.randomUUID().toString())
				.toJobParameters();
		
		return this.jobOperator.start(this.jobGestionClientesSQL, parametros);
	}

}
