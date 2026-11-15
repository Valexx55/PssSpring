package edu.pss.spring.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * En esta clase, declaramos nuestro JOB (bean) y los STEPS que lo componen
 * (también serán beans)
 * 
 * JOB - GestionarFichero
 * 
 * STEP 1 - Comprobar Fichero STEP 2 - Crear directorio Salida STEP 3 - Procesar
 * Fichero STEP 4 - Archivar Fichero
 */
@Configuration
public class ConfigJobCSV {



	// esto es una forma de instanciar un bean para el contexto de spring
	/**
	 * Este será el JOB o mi tarea principal en este flujo Batch
	 * 
	 * @return
	 */
	@Bean
	public Job jobGestionFichero(JobRepository jobRepository, Step comprobarFichero, Step crearDirectorioSalida,
			Step procesarFichero, Step archivarFichero, JobExecutionListener listenerInfoJob) {

		Job trabajoCompleto = null;

		trabajoCompleto = new JobBuilder("jobGestionFichero", jobRepository).start(comprobarFichero)
				.next(crearDirectorioSalida).next(procesarFichero).next(archivarFichero).listener(listenerInfoJob).build();

		return trabajoCompleto;
	}

	/**
	 * 
	 * @see @Qualifier Cuando en el contexto de spring tengo varios beans
	 *      candidatos, en ocasiones puede haber un problema porque Spring no
	 *      identifica al bean que yo quiero. En este caso, tengo varios Steps y con
	 *      el parámetro paso1, podrían coincidir varios candidatos. Usando esta
	 *      anotación, le digo a Spring que quiero el Step identificado por ese
	 *      nombre (que es el nombre del método)
	 */
	/*
	 * @Bean public Job jobGestionFichero(JobRepository
	 * jobRepository, @Qualifier("comprobarFichero") Step paso1, Step
	 * crearDirectorioSalida, Step procesarFichero, Step archivarFichero ) {
	 * 
	 * Job trabajoCompleto = null;
	 * 
	 * trabajoCompleto = new JobBuilder("jobGestionFichero", jobRepository)
	 * .start(paso1) .next(crearDirectorioSalida) .next(procesarFichero)
	 * .next(archivarFichero) .build();
	 * 
	 * return trabajoCompleto; }
	 */
}
