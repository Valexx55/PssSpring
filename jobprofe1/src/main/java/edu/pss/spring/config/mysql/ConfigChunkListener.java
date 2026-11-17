package edu.pss.spring.config.mysql;

import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.pss.spring.model.Cliente;

@Configuration
public class ConfigChunkListener {
	
	@Bean
	public ChunkListener<Cliente, Cliente> listenerChunks()
	{
		return new ChunkListener<Cliente, Cliente>() {
			
			@Override
			public void beforeChunk(Chunk<Cliente> chunk) {
				
				System.out.println("\n ===INICIO CHUNK===");
				int num_clientes = chunk.size();
				
				System.out.println("NUM CLIS CHUNK ini = " + num_clientes);
				
				chunk.forEach(cliente -> System.out.println(cliente));
			}
			
			@Override
			public void afterChunk(Chunk<Cliente> chunk) {
				System.out.println("\\n ===FIN CHUNK===");
				int num_clientes = chunk.size();
				
				System.out.println("NUM CLIS CHUNK fin = " + num_clientes);
			}
			
			@Override
			public void onChunkError(Exception exception, Chunk<Cliente> chunk) {
				
				System.err.println("\\n ===ERROR en CHUNK===");
				exception.printStackTrace();
				
			}
		};
		
	}

}
