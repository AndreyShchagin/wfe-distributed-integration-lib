/**
 *
 */
package org.camunda.distributed.integration.client.infrastructure;

import org.camunda.distributed.integration.client.commons.FileUtil;
import org.camunda.distributed.integration.client.configuration.WorkflowConfiguration;
import org.camunda.distributed.integration.client.queueresolver.WfSettingsRabbitConfiguration;
import org.camunda.distributed.integration.commons.dto.FileDto;
import org.camunda.distributed.integration.commons.exceptions.WfeConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.camunda.distributed.integration.commons.WfPropertyConstants.*;

/**
 * Service for resolving a workflow process file into Resource objects by getting the application specific documents
 * stored in remote "Back-office" service through a message broker
 *
 */
@Import({WfSettingsRabbitConfiguration.class, WorkflowConfiguration.class})
public class DistributedResourceResolver extends AWfeResourceResolver {

	@Autowired
	private Environment			environment;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ResourcePatternResolver	resourceLoader;


	@Override
	public Resource[] getResourcesAsStream(String... documentIds) {
		List<FileDto> files = getWorkflowProcessDescriptionFiles(documentIds);
		//Load files
		List<String> temporaryFilePaths = files.stream().map(wfFile -> FileUtil.uploadFileToLocation(
					new ByteArrayInputStream(
							wfFile.getFileContent()),
					FileUtil.getTemporaryPath(
							environment.getRequiredProperty(SETTINGS_TEMP_DOCUMENTS_ROOTPATH.name())).concat(wfFile.getName()))
		).collect(Collectors.toList());
		//Load resources from files
		//Clean up
		deleteTemporaryFiles(temporaryFilePaths);

		return temporaryFilePaths.stream().map(path ->
				resourceLoader.getResource("file:" + path)
		).toArray(Resource[]::new);
	}

	private List<FileDto> getWorkflowProcessDescriptionFiles(String... documentIds) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
				environment.getRequiredProperty(SETTINGS_SERVICE_URL.name()).trim().
						concat(environment.getRequiredProperty(SETTINGS_SERVICE_CONTEXT.name()).trim())
		);
		builder.path(environment.getRequiredProperty(SETTINGS_SERVICE_DOCUMENT_DISTRIBUTION_ENDPOINT_URL.name()));
		builder.queryParam("appextid", environment.getRequiredProperty(WorkflowConfiguration.APPLICATION_ID));
		builder.queryParam("documentid", Arrays.stream(documentIds).filter(doc -> doc != null && !doc.isEmpty()).collect(Collectors.toList()));

		try {
			URI requestUri = new URI((builder.toUriString()));
			ResponseEntity<List<FileDto>> lisOfDocuments = restTemplate.exchange(
					requestUri,
					HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<FileDto>>(){});
			return lisOfDocuments.getBody();
		} catch (URISyntaxException e) {
			throw new WfeConfigurationException("Malformed URI for the remote settings service", e);
		}
	}

	private void deleteTemporaryFiles(final List<String> tmpFilePaths) {
		long delayPerFile = Long.parseLong(environment.getProperty(UPLOAD_SERVICE_CLEANUP_DELAY.name(), "25"));
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		// wait for Camunda engine to finalize processing the resources before delete them
		executorService.schedule(() -> {tmpFilePaths.stream().forEach(p -> FileUtil.deleteFileFromLocation(p));}, tmpFilePaths.size() * delayPerFile, TimeUnit.SECONDS);
	}
}
