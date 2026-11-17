package edu.pss.spring.config.mysql;

import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.pss.spring.model.Cliente;

@Configuration
public class ConfigItemsMySQL {

	//ItemReader : lectura desde la Base de datos
	
	@Bean
	public JdbcCursorItemReader<Cliente> clienteReader(){
		return null;
	}
}
