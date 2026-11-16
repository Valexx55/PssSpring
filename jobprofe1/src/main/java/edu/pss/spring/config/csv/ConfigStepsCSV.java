package edu.pss.spring.config.csv;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConfigStepsCSV {

	@Value("${batch.ruta.entrada}") // con Value, SPRING inyecta el valor de la propieda definido en el fichero
	// properties
	private String rutaEntrada;

	@Value("${batch.ruta.procesados}")
	private String rutaProcesados;

	@Value("${batch.ruta.errores}")
	private String rutaErrores;

	@Value("${batch.fichero}")
	private String nombreFichero;


	@Bean
	public Step archivarFichero(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			StepExecutionListener listenerStep) {
		Step pasoArchivarFichero = null;

		pasoArchivarFichero = new StepBuilder("archivarFichero", jobRepository)
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("En archivarFichero");
					return RepeatStatus.FINISHED;
				}, transactionManager).listener(listenerStep).build();

		return pasoArchivarFichero;
	}

	@Bean
	public Step procesarFichero(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			StepExecutionListener listenerStep) {
		Step pasoProcesarFichero = null;

		pasoProcesarFichero = new StepBuilder("procesarFichero", jobRepository)
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("En procesarFichero");

					Path ficheroEntrada = Paths.get(this.rutaEntrada, this.nombreFichero);

					Path ficheroSalida = Paths.get(this.rutaProcesados, "clis_procesados.csv");

					Path ficheroErrores = Paths.get(this.rutaErrores, "clis_error.csv");

					/**
					 * Leer y procesar todos los registros, Más adelante, refinamos esto usando
					 * ItemReader, Writer y Proccessor
					 */

					List<String> lineas = Files.readAllLines(ficheroEntrada);

					List<String> lineasSalida = new ArrayList<>();// operador diamante <>

					List<String> lineasErrores = new ArrayList<>();

					int registrosLeidos = 0;
					int registrosCorrectos = 0;
					int registrosIncorrectos = 0;

					if (!lineas.isEmpty()) {
						String cabacera = lineas.get(0);

						lineasSalida.add(cabacera);
					}

					for (int indice = 1; indice < lineas.size(); indice++) {
						String linea = lineas.get(indice);
						registrosLeidos++;

						String[] campos = linea.split(";");

						if (campos.length != 4) {
							lineasErrores.add(linea);
							registrosIncorrectos++;
						} else {
						// caso correcto
						// aplicamos una transformación: NOMBRE en MAYUS, el EMAIL en minusculas, LA
						// ciudad en mayúsucalas, nueva propiedad activo=true

							String nombre = campos[1].trim().toUpperCase();
							String email = campos[2].trim().toLowerCase();
							String ciudad = campos[3].trim().toUpperCase();
							String id = campos[0].trim().toUpperCase();

							String lineaTx = id.trim() + ";" + nombre + ";" + email + ";" + ciudad + ";" + "true";

							lineasSalida.add(lineaTx);
							registrosCorrectos++;
							contribution.incrementReadCount(); // actualizamos en el contexto cuántos registros hemos
																// leído
						}
					}

							// Generamos Ficheros de salida

					if (registrosCorrectos > 0) {
						Files.write(ficheroSalida, lineasSalida, StandardCharsets.UTF_8);
					}

					if (registrosIncorrectos > 0) {
						Files.write(ficheroErrores, lineasErrores, StandardCharsets.UTF_8);
					}


					StepExecution stepExecution = contribution.getStepExecution();

					System.out.println("INFO STEP EXECUTION");
					System.out.println("ID = " + stepExecution.getId() + " NOMBRE " + stepExecution.getStepName()
							+ " STATUS " + stepExecution.getStatus());

						// Acceso al COntexto del paso, para guardar la info que yo quiera o necesite
					ExecutionContext contextoStep = stepExecution.getExecutionContext();

						// guardo información "custom" personalizada - variables mías
					contextoStep.put("registrosLeidos", registrosLeidos);
					contextoStep.put("registrosCorrectos", registrosCorrectos);
					contextoStep.put("registrosIncorrectos", registrosIncorrectos);

					// igualmente, puedo actualizar propiedades /valores que están predefinidos
					// dentro del contexto
					contribution.incrementWriteCount(registrosCorrectos);
					contribution.incrementFilterCount(registrosIncorrectos);

					return RepeatStatus.FINISHED;
				}, transactionManager).listener(listenerStep)// estoy asociando un listener a este step
				.build();

		return pasoProcesarFichero;
	}

	@Bean
	public Step crearDirectorioSalida(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			StepExecutionListener listenerStep) {
		Step pasoCrearDirectorioSalida = null;

		pasoCrearDirectorioSalida = new StepBuilder("crearDirectorioSalida", jobRepository)
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("En crearDirectorioSalida");

					Path dirProcesados = Paths.get(this.rutaProcesados);
					Path dirErrores = Paths.get(this.rutaErrores);

					Files.createDirectories(dirProcesados);
					Files.createDirectories(dirErrores);

					System.out.println("Directorios de trabajo listos");
					return RepeatStatus.FINISHED;
				}, transactionManager).listener(listenerStep).build();

		return pasoCrearDirectorioSalida;
	}

	@Bean
	public Step comprobarFichero(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			StepExecutionListener listenerStep) {
		Step stepComprobarFichero = null;

		stepComprobarFichero = new StepBuilder("comprobarFichero", jobRepository)
				.tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
					System.out.println("En comprobar Fichero");

					Path fichero = Paths.get(this.rutaEntrada, this.nombreFichero);

					if (!Files.exists(fichero)) {
						throw new RuntimeException("No existe el fichero " + fichero);

					} else {
						System.out.println("Fichero encontrado");
					}

					return RepeatStatus.FINISHED;
				}, transactionManager).listener(listenerStep).build();

		return stepComprobarFichero;
	}

}
