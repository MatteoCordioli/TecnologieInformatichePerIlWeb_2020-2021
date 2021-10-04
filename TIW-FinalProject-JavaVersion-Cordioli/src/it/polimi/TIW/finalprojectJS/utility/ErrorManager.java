package it.polimi.TIW.finalprojectJS.utility;

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
	
	public void setError(int codeError, String message, HttpServletResponse response) throws IOException {
		response.setStatus(codeError);
		response.getWriter().println(message);
	}
	public void eraseErrorInfo() {
		this.errorCode = 0;
		this.errorString = "No Errors found!";
	}
	
}
