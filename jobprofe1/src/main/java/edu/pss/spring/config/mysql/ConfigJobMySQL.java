package edu.pss.spring.config.mysql;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigJobMySQL {

	@Bean
	public Job jobGestionClientesSQL(
			JobRepository jobRepository,
			@Qualifier("procesarClientesSQL") Step procesarClientesSQL,
			@Qualifier("listenerJob") JobExecutionListener listenerJob)
	{
		Job trabajoMySQL = null;
		
			trabajoMySQL = new JobBuilder("jobGestionClientesSQL", jobRepository)
					.start(procesarClientesSQL)
					.listener(listenerJob)
					.build();
		
		return trabajoMySQL;
	}
}
