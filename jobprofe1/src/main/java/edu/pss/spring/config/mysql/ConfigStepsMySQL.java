package edu.pss.spring.config.mysql;



import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import edu.pss.spring.model.Cliente;

@Configuration
public class ConfigStepsMySQL {

	@Bean
	public Step procesarClientesSQL(
			JobRepository jobRepository, 
			PlatformTransactionManager transactionManager,
			JdbcCursorItemReader<Cliente> clienteReader,
			JdbcBatchItemWriter<Cliente> clienteWriter,
			ItemProcessor<Cliente, Cliente> clienteProcesor,
			@Qualifier("listenerStep") StepExecutionListener listenerStep)
			
	{
		Step pasoMysql = null;
		
			pasoMysql = new StepBuilder("procesarClientesSQL", jobRepository)
					.<Cliente, Cliente>chunk(10)
					.transactionManager(transactionManager)
					.reader(clienteReader)
					.processor(clienteProcesor)
					.writer(clienteWriter)
					.listener(listenerStep)
					.build();
			
		
		return pasoMysql;
	}
	
}
