package edu.pss.spring.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * En esta clase, declaramos nuestro JOB (bean) y los STEPS que lo componen (también serán beans)
 * 
 * JOB - GestionarFichero
 *  
 *  	STEP 1 - Comprobar Fichero
 *  	STEP 2 - Crear directorio Salida 
 *  	STEP 3 - Procesar Fichero
 *  	STEP 4 - Archivar Fichero
 */
@Configuration
public class ConfigBatch1 {

	
	@Bean
	public Step archivarFichero()
	{
		return null;
	}
	
	
	@Bean
	public Step procesarFichero()
	{
		return null;
	}
	
	@Bean
	public Step crearDirectorioSalida()
	{
		return null;
	}
	
	
	@Bean
	public Step comprobarFichero()
	{
		return null;
	}
	
	//esto es una forma de instanciar un bean para el contexto de spring
	/**
	 * Este será el JOB o mi tarea principal en este flujo Batch
	 * @return
	 */
	@Bean
	public Job jobGestionFichero()
	{
		return null;
	}
}
