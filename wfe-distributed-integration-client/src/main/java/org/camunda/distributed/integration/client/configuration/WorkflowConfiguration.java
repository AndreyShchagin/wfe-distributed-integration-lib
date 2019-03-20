package org.camunda.distributed.integration.client.configuration;

import javax.sql.DataSource;

import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.distributed.integration.client.infrastructure.WfeResourceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


/**

 */
@Configuration
@ComponentScan("org.camunda.distributed.integration.client.infrastructure")
public abstract class WorkflowConfiguration {

	public static final String APPLICATION_ID = "application.id";

	public abstract DataSource dataSource();

	public abstract PlatformTransactionManager platformTransactionManager();

	public abstract WfeResourceResolver getWfeResourceResolver();

	private SpringProcessEngineConfiguration processEngineConfiguration() {

		SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
		config.setDataSource(dataSource());
		config.setTransactionManager(platformTransactionManager());

		config.setDatabaseSchemaUpdate(SpringProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		config.setJobExecutorActivate(true);
		config.setHistory(getHistoryLevel());

		config.setDeploymentResources(getWfeResourceResolver().getResourcesAsStream());

		return config;
	}

	protected String getHistoryLevel() {
		return SpringProcessEngineConfiguration.HISTORY_NONE;
	}

	@Bean
	public ProcessEngineFactoryBean processEngine() {
		ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
		factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
		return factoryBean;
	}

	@Bean
	public DmnEngine getDmnEngine() {
		DefaultDmnEngineConfiguration configuration = (DefaultDmnEngineConfiguration)DmnEngineConfiguration.createDefaultDmnEngineConfiguration();
		return configuration.buildEngine();
	}

	@Bean
	public RepositoryService repositoryService(ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}

	@Bean
	public RuntimeService runtimeService(ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}

	@Bean
	public TaskService taskService(ProcessEngine processEngine) {
		return processEngine.getTaskService();
	}

}
