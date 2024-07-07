package com.abhishek.fileserver.models;

public class APIMessageResponse {
	private String message;
	private int status;

	public APIMessageResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	// Getters and Setters
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
