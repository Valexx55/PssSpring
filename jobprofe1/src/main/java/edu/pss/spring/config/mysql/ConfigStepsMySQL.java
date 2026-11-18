
package edu.pss.spring.config.mysql;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.core.listener.StepExecutionListener;
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
            ItemProcessor<Cliente, Cliente> clienteProcessor,
            JdbcBatchItemWriter<Cliente> clienteWriter,
            @Qualifier("listenerInfoStep")
            StepExecutionListener listenerInfoStep,
            @Qualifier("listenerChunks")
            ChunkListener<Cliente, Cliente> listenerChunks) {

        return new StepBuilder("procesarClientesSQL", jobRepository)

                .<Cliente, Cliente>chunk(7)

                .transactionManager(transactionManager)

                .reader(clienteReader)

                .processor(clienteProcessor)

                .writer(clienteWriter)

                .listener(listenerInfoStep)
                .listener(listenerChunks)

                .build();
    }
}
