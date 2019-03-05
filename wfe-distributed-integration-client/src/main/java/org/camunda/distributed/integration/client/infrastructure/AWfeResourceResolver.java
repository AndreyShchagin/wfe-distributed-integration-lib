/**
 * 
 */
package org.camunda.distributed.integration.client.infrastructure;

import org.springframework.core.io.Resource;

/**
 * Abstraction for WFE resource loader.
 * Concrete implementation shall be defined in application's configuration
 */
public abstract class AWfeResourceResolver {

	public abstract Resource[] getResourcesAsStream(String... documentId);
}
