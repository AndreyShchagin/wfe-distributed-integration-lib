package org.camunda.distributed.integration.commons.dto;

public class FileDto {

	private String	name;
	private String	mimeType;
	private byte[]	fileContent;

	public FileDto() {}

	public FileDto(String name, String contentType, byte[] fileContent) {
		super();
		this.name = name;
		this.mimeType = contentType;
		this.fileContent = fileContent;
	}

	public FileDto(String name, String mimeType) {
		this.name = name;
		this.mimeType = mimeType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

}
