package edu.pss.spring.config.csv;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigListenersCSV {
	
	
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

}
