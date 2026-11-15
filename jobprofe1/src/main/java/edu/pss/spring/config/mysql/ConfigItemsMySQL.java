
package edu.pss.spring.config.mysql;

import javax.sql.DataSource;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.pss.spring.model.Cliente;

@Configuration
public class ConfigItemsMySQL {

    // ITEM READER: lectura desde MySQL
    @Bean
    public JdbcCursorItemReader<Cliente> clienteReader(
            DataSource dataSource) {

        return new JdbcCursorItemReaderBuilder<Cliente>()
                .name("clienteReader")
                .dataSource(dataSource)
                .sql("""
                    SELECT id, nombre, email, ciudad
                    FROM clientes_origen
                    ORDER BY id
                    """)
                .rowMapper((rs, rowNum) -> new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("ciudad"),
                        false
                ))
                .build();
    }

    // ITEM PROCESSOR: transformacion de los clientes
    @Bean
    public ItemProcessor<Cliente, Cliente> clienteProcessor( @Value("${batch.demo.fallar:false}") boolean provocarError) {

    	 
    	
    	return cliente -> {

            if (provocarError && cliente.id() == 18) {
                throw new IllegalStateException(
                    "ERROR SIMULADO: cliente ID 18"
                );
            }

            return new Cliente(
                cliente.id(),
                cliente.nombre().toUpperCase(),
                cliente.email().toLowerCase(),
                cliente.ciudad().toUpperCase(),
                true
            );
        };
    }

    // ITEM WRITER: escritura en MySQL
    @Bean
    public JdbcBatchItemWriter<Cliente> clienteWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Cliente>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO clientes_procesados
                        (id, nombre, email, ciudad, activo)
                    VALUES (?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        nombre = VALUES(nombre),
                        email = VALUES(email),
                        ciudad = VALUES(ciudad),
                        activo = VALUES(activo)
                    """)
                .itemPreparedStatementSetter((cliente, ps) -> {
                    ps.setInt(1, cliente.id());
                    ps.setString(2, cliente.nombre());
                    ps.setString(3, cliente.email());
                    ps.setString(4, cliente.ciudad());
                    ps.setBoolean(5, cliente.activo());
                })
                .build();
    }
}
