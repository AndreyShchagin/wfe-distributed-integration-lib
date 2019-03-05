/**
 * 
 */
package org.camunda.distributed.integration.client.infrastructure;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Service for adding new resources
 * 

 */
@Service
public class ResourceUpdater {

	@Autowired
	private AWfeResourceResolver resourceResolver;

	@Autowired
	private RepositoryService	repositoryService;

	public void addResources(String... documentId) throws IOException {
		Resource[] resources = resourceResolver.getResourcesAsStream(documentId);

		if (resources.length == 0) {
			return;
		}
		DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().enableDuplicateFiltering(false).name("ResourceUpdater");
		for (Resource resource: resources) {
			InputStream targetStream = new FileInputStream(resource.getFile());
			deploymentBuilder.addInputStream(resource.getFilename(), targetStream);
			targetStream.close();
		}
		deploymentBuilder.deploy();
	}
}
