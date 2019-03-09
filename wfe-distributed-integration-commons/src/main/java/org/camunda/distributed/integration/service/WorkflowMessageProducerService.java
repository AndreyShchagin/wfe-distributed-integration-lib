package org.camunda.distributed.integration.service;

import org.camunda.distributed.integration.commons.WfPropertyConstants;
import org.camunda.distributed.integration.commons.dto.WfProcessModelDto;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

public abstract class WorkflowMessageProducerService {

    /**
     * Sends the notification to the message broker saying that the document with certain UUID is ready to be processed
     * @param application - application routing key
     * @param docUUIDs - ID of a document
     */
    public void sendMessage(String application, List<String> docUUIDs){
        String routingKey = WfPropertyConstants.WFE_ROUTING_KEY.name() + "." + application.toLowerCase() + ".app";
        getRabbitTemplate().convertAndSend(getTopicExchange().getName(), routingKey, new WfProcessModelDto(docUUIDs));
        }

    protected abstract TopicExchange getTopicExchange();

    protected abstract RabbitTemplate getRabbitTemplate();
}
