/**
 * 
 */
package org.camunda.distributed.integration.client.infrastructure;

import org.springframework.core.io.Resource;

/**
 * Abstraction for WFE resource loader.
 * Concrete implementation shall be defined in application's configuration
 */
public interface WfeResourceResolver {

	Resource[] getResourcesAsStream(String... documentId);
}
