package edu.pss.spring.service;

import java.util.UUID;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
	
	JobOperator jobOperator;
	Job jobGestionFichero;
	
	//joboperator nos permite laznar trabajos progrmáticamente
	public BatchService (JobOperator jobOperator, @Qualifier("jobGestionFichero") Job jobGestionFichero)
	{
		this.jobOperator = jobOperator;
		this.jobGestionFichero = jobGestionFichero;
	}
	
	//propagamos la execpción
	public JobExecution ejecutarFicheroBatchCSV () throws Exception
	{
		
		JobParameters parametros = new JobParametersBuilder().addString("idSolicitud", UUID.randomUUID().toString()).toJobParameters();
		return this.jobOperator.start(this.jobGestionFichero, parametros);
	}

}
