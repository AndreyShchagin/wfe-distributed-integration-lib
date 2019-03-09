package org.camunda.distributed.integration.commons.dto;

import java.util.List;

public class WfProcessModelDto {

	private List<String> docUUIDs;

	public WfProcessModelDto(List<String> docUUIDs) {
		super();
		this.docUUIDs = docUUIDs;
	}

	public List<String> getDocUUIDs() {
		return docUUIDs;
	}

	public void setDocUUIDs(List<String> docUUIDs) {
		this.docUUIDs = docUUIDs;
	}

}
