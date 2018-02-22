package hu.beni.tester.config;

import java.util.concurrent.Executor;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import hu.beni.tester.archive.ArchiveReceiver;

@Configuration
@EnableAsync
public class AsyncConfig {
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

	@Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build()
        		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
	
	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.initialize();
		return executor;
	}
	
	@Bean
	public Queue queue() {
		return new Queue("archiveAmusementPark", false);
	}
	
	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames("archiveAmusementPark");
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(ArchiveReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receive");
	}
}