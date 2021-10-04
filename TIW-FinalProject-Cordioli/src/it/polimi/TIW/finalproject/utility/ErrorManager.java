package it.polimi.TIW.finalproject.utility;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ErrorManager {
	public static ErrorManager instance = new ErrorManager();
	private int errorCode;
	private String errorString;
	
	public ErrorManager() {
		
	}
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorString() {
		return errorString;
	}

	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	
	public void setError(int codeError, String message, HttpServletResponse response) {
		this.errorCode = codeError;
		this.errorString = message;
		try {
			response.sendRedirect("Error");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void eraseErrorInfo() {
		this.errorCode = 0;
		this.errorString = "No Errors found!";
	}
	
}
