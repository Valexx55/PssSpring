package edu.pss.spring.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class ConfigBatch1 {

	@Bean
	public Step archivarFichero(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step pasoArchivarFichero = null;

		pasoArchivarFichero = new StepBuilder("archivarFichero", jobRepository)
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("En archivarFichero");
					return RepeatStatus.FINISHED;
				}, transactionManager).build();

		return pasoArchivarFichero;
	}

	@Bean
	public Step procesarFichero(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step pasoProcesarFichero = null;

			pasoProcesarFichero = new StepBuilder("procesarFichero", jobRepository)
					.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
						System.out.println("En procesarFichero");
						
						throw new RuntimeException("Fallo procesando el fichero");
						
						//return RepeatStatus.FINISHED;
					}, transactionManager).build();

	    return pasoProcesarFichero;
	}

	@Bean
	public Step crearDirectorioSalida(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step pasoCrearDirectorioSalida = null;

			pasoCrearDirectorioSalida = new StepBuilder("crearDirectorioSalida", jobRepository)
					.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
						System.out.println("En crearDirectorioSalida");
						return RepeatStatus.FINISHED;
					}, transactionManager).build();

		return pasoCrearDirectorioSalida;
	}

	@Bean
	public Step comprobarFichero (JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step stepComprobarFichero = null;

			stepComprobarFichero = new StepBuilder("comprobarFichero", jobRepository)
					.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
						System.out.println("En comprobar Fichero");
						return RepeatStatus.FINISHED;
					}, transactionManager).build();

		return stepComprobarFichero;
	}

	// esto es una forma de instanciar un bean para el contexto de spring
	/**
	 * Este será el JOB o mi tarea principal en este flujo Batch
	 * 
	 * @return
	 */
	@Bean
	public Job jobGestionFichero(JobRepository jobRepository, Step comprobarFichero, 
			Step crearDirectorioSalida, Step procesarFichero, Step archivarFichero
			) {
		
		Job trabajoCompleto = null;
		
			trabajoCompleto = new JobBuilder("jobGestionFichero", jobRepository)
					.start(comprobarFichero)
					.next(crearDirectorioSalida)
					.next(procesarFichero)
					.next(archivarFichero)
					.build();
	
		return trabajoCompleto;
	}
	
	
	/**
	 * 
	 * @see @Qualifier Cuando en el contexto de spring tengo varios beans candidatos, en ocasiones puede haber un problema
	 * porque Spring no identifica al bean que yo quiero. En este caso, tengo varios Steps y con el parámetro paso1, podrían
	 * coincidir varios candidatos. Usando esta anotación, le digo a Spring que quiero el Step identificado por ese nombre
	 * (que es el nombre del método)
	 */
	/*
	@Bean
	public Job jobGestionFichero(JobRepository jobRepository, @Qualifier("comprobarFichero") Step  paso1, 
			Step crearDirectorioSalida, Step procesarFichero, Step archivarFichero
			) {
		
		Job trabajoCompleto = null;
		
			trabajoCompleto = new JobBuilder("jobGestionFichero", jobRepository)
					.start(paso1)
					.next(crearDirectorioSalida)
					.next(procesarFichero)
					.next(archivarFichero)
					.build();
	
		return trabajoCompleto;
	}
	*/
}
