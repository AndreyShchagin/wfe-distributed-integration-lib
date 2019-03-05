package org.camunda.distributed.integration.commons.exceptions;

public class GenericWfeIntegrationException extends RuntimeException {

    private final String serviceId;


    public GenericWfeIntegrationException(String message) {
        this("wfe-integration-library", message, null);
    }

    public GenericWfeIntegrationException(String message, Throwable cause) {
        this("wfe-integration-library", message, cause);
    }

    protected GenericWfeIntegrationException(String serviceId, String message) {
        this(serviceId, message, null);
    }

    protected GenericWfeIntegrationException(String serviceId, String message, Throwable cause) {
        super(message, cause);
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

}
