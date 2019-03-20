package org.camunda.distributed.integration.client.infrastructure;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {LocalResourceResolver.class})
public class LocalResourceResolverTest {
    @Configuration
    static class TestConfig {
        @Bean
        public LocalResourceResolver localResourceResolver() {
            return Mockito.mock(LocalResourceResolver.class);
        }

    }

    @Autowired
    LocalResourceResolver localResourceResolver;

    private static String BPMN_LOCAL_RESOURCE = "wfeIntegrationExample.bpmn";
    private static String DMN_LOCAL_RESOURCE = "wfeIntegrationExample.dmn";

    @Test
    public void getSingleResourcesAsStream() {
        Resource[] resources = localResourceResolver.getResourcesAsStream(BPMN_LOCAL_RESOURCE);
        Assert.assertNotNull(resources);
        Assert.assertThat(resources.length, is(1));
        Assert.assertThat(resources[0].getFilename(), is(BPMN_LOCAL_RESOURCE));
    }

    @Test
    public void getResourcesAsStream() {
        Resource[] resources = localResourceResolver.getResourcesAsStream(BPMN_LOCAL_RESOURCE, DMN_LOCAL_RESOURCE);
        Assert.assertNotNull(resources);
        Assert.assertThat(resources.length, is(2));
        Assert.assertThat(resources[0].getFilename(), is(BPMN_LOCAL_RESOURCE));
        Assert.assertThat(resources[1].getFilename(), is(DMN_LOCAL_RESOURCE));
    }
}