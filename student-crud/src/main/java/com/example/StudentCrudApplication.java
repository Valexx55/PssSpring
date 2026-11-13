
package com.example;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication // Marcaba a SpringBoot que es la clase de configuración global del proyecto, la
						// que arraca el contexto y ejecuta la app
public class StudentCrudApplication {
	public static void main(String[] args) {
		ApplicationContext ac = SpringApplication.run(StudentCrudApplication.class, args);
		mostrarContexto(ac);
	}

	private static void mostrarContexto(ApplicationContext ac) {

		String[] beans = ac.getBeanDefinitionNames();

		Arrays.sort(beans);

		System.out.println("\n===== BEANS DE SPRING =====");

		for (String bean : beans) {
			System.out.println(bean);
			Class<?> tipo = ac.getType(bean);

			System.out.printf("%-45s %s%n", bean, tipo != null ? tipo.getSimpleName() : "Desconocido");
		}
		System.out.println("TOTAL: " + beans.length);

	}
}
