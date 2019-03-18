package org.camunda.distributed.integration.client.configuration;

import java.util.Collections;
import java.util.List;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.StringUtils;

/**
 * Rabbit configuration for the WFE distributed service
 */
@Configuration
@ComponentScan("org.camunda.distributed.integration.client.queueresolver")
@EnableRabbit
public class WfSettingsRabbitConfiguration implements RabbitListenerConfigurer {

	/**
	 * WFE topic exchange;
	 * <p>
	 * A message sent with a particular routing key will be delivered to all the queues that are bound with a matching binding key. However there are two important special cases for binding keys:
	 * <p>
	 * <ul>
	 * <li>* (star) can substitute for exactly one word.</li>
	 * <li># (hash) can substitute for zero or more words.</li>
	 * </ul>
	 * It's used to exchange specific workflow engine messages
	 */
	public static final String WFE_SETTINGS_TOPIC_EXCHANGE = ".wfe.settings.topic.exchange";

	public static final String	ROUTING_KEY				= "wfe.settings";

	@Autowired
	private Environment			environment;

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(environment.getProperty("rabbitmq.host"));
		connectionFactory.setUsername(environment.getProperty("rabbitmq.user"));
		connectionFactory.setPassword(environment.getProperty("rabbitmq.password"));
		connectionFactory.setPort(Integer.valueOf(environment.getProperty("rabbitmq.port")));
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}

	/**
	 * Creates a queue for the current consumer
	 * <p>
	 * The queue is exclusive (queue becomes private and can only be consumed by the current app) and auto-delete (the queue is automatically deleted when the consumer unsubscribes); when the
	 * application is replicated, each instance has it's own queue so it can consume the message and update the cached data
	 */
	@Bean
	public Queue wfeSettingsQueue() {
		if (StringUtils.isEmpty(environment.getProperty(WorkflowConfiguration.APPLICATION_ID))) {
			throw new BeanCreationException("Cannot configure consumer queue, please set the property `" + WorkflowConfiguration.APPLICATION_ID + "` to your application properties file ");
		}
		return new Queue(getCurrentQueueName(), false, false, true);
	}

	/**
	 * Gets the current consumer queue name
	 * 
	 * @return
	 */
	public String getCurrentQueueName() {
		return getQueuesPrefix() + "." + ROUTING_KEY + "." + environment.getProperty(WorkflowConfiguration.APPLICATION_ID).toLowerCase() + ".app";
	}

	@Bean
	public TopicExchange wfeTopicExchange() {
		return new TopicExchange(getQueuesPrefix() + WFE_SETTINGS_TOPIC_EXCHANGE);
	}

	/**
	 * Links the current queue to the p2p wfe exchange in order to also receive the messages send by p2p application
	 */
	@Bean()
	public List<Binding> wfeBindings() {
		Binding topicExchangeBinding = BindingBuilder.bind(wfeSettingsQueue()).to(wfeTopicExchange()).with("#." + environment.getProperty(WorkflowConfiguration.APPLICATION_ID).toLowerCase() + ".#");
		return Collections.singletonList(topicExchangeBinding);
	}

	@Bean
	public RabbitAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public SimpleMessageListenerContainer listenerContainer() {
		SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
		listenerContainer.setConnectionFactory(connectionFactory());
		listenerContainer.setQueues(wfeSettingsQueue());
		listenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
		listenerContainer.setMessageConverter(messageConverter());
		return listenerContainer;
	}

	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
		registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
	}

	@Bean
	public MappingJackson2MessageConverter jackson2Converter() {
		return new MappingJackson2MessageConverter();
	}

	@Bean
	public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
		DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
		factory.setMessageConverter(jackson2Converter());
		return factory;
	}

	public String getQueuesPrefix() {
		return environment.getProperty("rabbitmq.queues.prefix");
	}
}
