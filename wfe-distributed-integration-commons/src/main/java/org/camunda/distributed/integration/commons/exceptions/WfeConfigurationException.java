package org.camunda.distributed.integration.commons.exceptions;

import java.net.URISyntaxException;

public class WfeConfigurationException extends GenericWfeIntegrationException {
    public WfeConfigurationException(String s, URISyntaxException e) {
        super(s, e);
    }
}
