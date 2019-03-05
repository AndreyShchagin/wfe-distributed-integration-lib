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
public class LocalResourceResolver extends AWfeResourceResolver {
    @Autowired
    private ResourcePatternResolver resourceLoader;

    private final String[] resources;

    public LocalResourceResolver(String[] resources){
        this.resources = resources;
    }
    @Override
    public Resource[] getResourcesAsStream(String... documentName) {
        //                        forEach(res -> resourceList.add(resourceLoader.getResource(res))
//                );
        return Stream.concat(Arrays.stream(resources), Arrays.stream(documentName)).
                filter(res -> res != null && !res.isEmpty()).
                map(res -> resourceLoader.getResource(res)).toArray(Resource[]::new);
    }
}
