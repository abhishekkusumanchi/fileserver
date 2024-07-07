package com.abhishek.fileserver.models;


public class ResponseMessage {
	private boolean result;
	private String message;

	// Getters and Setters
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
