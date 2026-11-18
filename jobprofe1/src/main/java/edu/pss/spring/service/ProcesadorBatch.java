package edu.pss.spring.service;

import org.springframework.batch.core.job.JobExecution;

public interface ProcesadorBatch {
	
	JobExecution ejecutar() throws Exception;

}
