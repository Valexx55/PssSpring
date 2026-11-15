
package edu.pss.spring.config;

import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.pss.spring.Cliente;

@Configuration
public class ConfigChunkListener {

    @Bean
    public ChunkListener<Cliente, Cliente> listenerChunks() {

        return new ChunkListener<Cliente, Cliente>() {

            @Override
            public void beforeChunk(Chunk<Cliente> chunk) {

                System.out.println("\n===== INICIO CHUNK =====");

                System.out.println("Clientes leídos: "
                        + chunk.size());

                chunk.forEach(cliente ->
                    System.out.println(
                        "Leyendo cliente ID: " + cliente.id()
                    )
                );
            }

            @Override
            public void afterChunk(Chunk<Cliente> chunk) {

                System.out.println("===== FIN CHUNK =====");

                System.out.println("Clientes escritos: "
                        + chunk.size());
            }

            @Override
            public void onChunkError(
                    Exception exception,
                    Chunk<Cliente> chunk) {

                System.out.println("===== ERROR EN CHUNK =====");

                System.out.println("Error: "
                        + exception.getMessage());
            }
        };
    }
}
