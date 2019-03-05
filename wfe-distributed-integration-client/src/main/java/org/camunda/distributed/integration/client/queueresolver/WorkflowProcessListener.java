package org.camunda.distributed.integration.client.queueresolver;

import org.camunda.distributed.integration.client.infrastructure.ResourceUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Listener service for the message broker
 */
@Service
public class WorkflowProcessListener {

	@Autowired
	private ResourceUpdater resourceUpdater;

	private static final Logger log = LoggerFactory.getLogger(WorkflowProcessListener.class);

	@RabbitListener(queues = "#{@wfeIntegrationQueue}")
	public void processMessage(WfProcessModel wfProcessModel) {
		log.info("Start processing wfe settings message");

		try {
			String[] idSize = new String[wfProcessModel.getDocUUIDs().size()];
			resourceUpdater.addResources(wfProcessModel.getDocUUIDs().toArray(idSize));
		}
		catch (Exception e) {
			log.error("Exception processing wfe message", e);
			throw new AmqpRejectAndDontRequeueException(e.getMessage());
		}
	}
}
