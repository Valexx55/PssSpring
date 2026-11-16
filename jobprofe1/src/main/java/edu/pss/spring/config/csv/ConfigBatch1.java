package edu.pss.spring.config.csv;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
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
//Al refactorizar el proyecto (lo dividimos en más clases), comentamos esta clase para que no sea tenida
//en cuenta por Spring al arranque y evitamos fallos por duplicidad de beans
//@Configuration
public class ConfigBatch1 {

	@Value("${batch.ruta.entrada}") // con Value, SPRING inyecta el valor de la propieda definido en el fichero
									// properties
	private String rutaEntrada;

	@Value("${batch.ruta.procesados}")
	private String rutaProcesados;

	@Value("${batch.ruta.errores}")
	private String rutaErrores;

	@Value("${batch.fichero}")
	private String nombreFichero;

	// deifnimos un listener para el JOB

	@Bean
	public JobExecutionListener listenerJob() {
		return new JobExecutionListener() {

			@Override
			public void beforeJob(JobExecution jobExecution) {
				System.out.println("\n===== INICIO DEL JOB =====");

				System.out.println("Job: " + jobExecution.getJobInstance().getJobName());

				System.out.println("ID ejecución: " + jobExecution.getId());

				System.out.println("Estado: " + jobExecution.getStatus());
			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				 System.out.println("\n===== RESUMEN FINAL DEL JOB =====");

		            System.out.println("Job: "
		                    + jobExecution.getJobInstance().getJobName());

		            System.out.println("ID ejecución: "
		                    + jobExecution.getId());

		            System.out.println("Estado final: "
		                    + jobExecution.getStatus());
		            
		            
		            ExecutionContext jobContext =
		                    jobExecution.getExecutionContext();
		            

		            System.out.println("Steps iniciados: "
		                    + jobContext.getInt("stepsIniciados", 0));
		            
		            
		            System.out.println("Último Step: "
		                    + jobContext.getString("nombreUltimoStep", "NINGUNO"));
		            
		            //recorrido de todos los valores guardados en el contexto del trabajo
		            jobContext.entrySet().forEach(entrada ->
	                System.out.println(
	                    entrada.getKey() + " = " + entrada.getValue()
	                )
	            );
		            
		            
		         // Resumen de las ejecuciones de los Steps
		            System.out.println("\n--- STEPS EJECUTADOS ---");

		            for (StepExecution step : jobExecution.getStepExecutions()) {

		                System.out.println("\nStep: "
		                        + step.getStepName());

		                System.out.println("ID: " + step.getId());
		                System.out.println("Estado: " + step.getStatus());

		                System.out.println("Leídos: " + step.getReadCount());
		                System.out.println("Escritos: " + step.getWriteCount());
		                System.out.println("Filtrados: " + step.getFilterCount());

		                // Contexto propio de cada Step
		                ExecutionContext stepContext =
		                        step.getExecutionContext();

		                stepContext.entrySet().forEach(entrada ->
		                    System.out.println(
		                        entrada.getKey() + " = " + entrada.getValue()
		                    )
		                );
		                
		                
		            }

		            System.out.println("\n===== FIN DEL RESUMEN =====");
			}
		};
	}

	// deifnimos un listener para que escuche el ciclo de vida del paso: el antes de
	// ejecutarse y el después

	@Bean
	public StepExecutionListener listenerStep() {

		return new StepExecutionListener() {

			@Override
			public void beforeStep(StepExecution stepExecution) {
				// TODO Auto-generated method stub
				// StepExecutionListener.super.beforeStep(stepExecution);
				System.out.println("\n=== INICIO DEL STEP ===");
				String resumenPre = stepExecution.getSummary();
				System.out.println(resumenPre);

				// Accedemos al COntexto del JOB
				// según se vayan lanzando pasos de este trabajo, vamos contándolos y lo
				// actualizamos
				// en el contexto del job
				ExecutionContext jobContexto = stepExecution.getJobExecution().getExecutionContext();
				int stepsIniciados = jobContexto.getInt("stepsIniciados", 0);
				stepsIniciados = stepsIniciados + 1;
				jobContexto.put("stepsIniciados", stepsIniciados);
				// el nombrel del último job iniciado
				jobContexto.put("nombreUltimoStep", stepExecution.getStepName());
			}

			@Override
			public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
				// TODO Auto-generated method stub

				System.out.println("\n=== FIN DEL STEP ===");
				String resumenPost = stepExecution.getSummary();
				System.out.println(resumenPost);

				System.out.println("\n=== CONTEXTO DEL STEP ===");

				ExecutionContext contextoStep = stepExecution.getExecutionContext();
				//== NO!, siempre equals para comparar objectos
				if (stepExecution.getStepName().equals("procesarFichero"))
				{
					System.out.println("REGISTROS CORRECTOS = " + contextoStep.getInt("registrosCorrectos", 0));
					System.out.println("REGISTROS INCORRECTOS = " + contextoStep.getInt("registrosIncorrectos", 0));
					System.out.println("REGISTROS LEIDOS = " + contextoStep.getInt("registrosLeidos", 0));

				} 
				
				//else sin info específica del paso
				
				stepExecution.getFailureExceptions().forEach(e -> System.err.println("ERROR " + e.getMessage()));

				return null;
			}

		};
	}

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

					// TODO actualizar StepExecutionContext y JobExecutionContext

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
