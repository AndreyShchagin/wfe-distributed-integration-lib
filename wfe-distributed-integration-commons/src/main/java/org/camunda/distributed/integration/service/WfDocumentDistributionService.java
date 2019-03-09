package org.camunda.distributed.integration.service;

import org.camunda.distributed.integration.commons.dto.FileDto;

import java.util.List;

/**
 * This interface shall be implemented by the settings service in order for client to be able to download BPMNs and DMN documents
 * which were uploaded to the settings service
 */
public interface WfDocumentDistributionService {
    List<FileDto> getWfDocumentsByApplicationId(String appExtId, List<String> documentIds);
}
