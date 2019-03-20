package org.camunda.distributed.integration.client.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Load BPMN/DMN models from local file resources
 *
 * @author Andrey Shchagin on 13.09.17.
 */
public class LocalResourceResolver implements WfeResourceResolver {
    @Autowired
    private ResourcePatternResolver resourceLoader;

    private final String[] resources;

    /**
     * Default constructor - load with empty resource set
     */
    public LocalResourceResolver(){
        this.resources = new String[0];
    }

    public LocalResourceResolver(String[] resources){
        this.resources = resources;
    }

    @Override
    public Resource[] getResourcesAsStream(String... documentName) {
        return Stream.concat(Arrays.stream(resources), Arrays.stream(documentName)).
                filter(res -> res != null && !res.isEmpty()).
                map(res -> resourceLoader.getResource(res)).toArray(Resource[]::new);
    }

    public String[] getResources() {
        return resources;
    }
}
