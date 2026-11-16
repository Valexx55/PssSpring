package edu.pss.spring.config.csv;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
				Step procesarFichero, Step archivarFichero, JobExecutionListener listenerJob) {

			Job trabajoCompleto = null;

			trabajoCompleto = new JobBuilder("jobGestionFichero", jobRepository).start(comprobarFichero)
					.next(crearDirectorioSalida).next(procesarFichero).next(archivarFichero).listener(listenerJob).build();

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
