package it.polimi.TIW.finalprojectJS.utility;

import java.time.LocalDate;

import javax.servlet.http.Part;

public class CheckVariable {
	public static CheckVariable instance = new CheckVariable();
	
	public CheckVariable() {
		
	}

	public boolean badFormatString(String string) {
		if(isNull(string))
			return true;
		else if(string.isEmpty())		//non messe in or perchè se è null non deve neanche provare ad accedere a una funzione di String
			return true;
		return false;
	}
	
	public boolean badFormatFile(Part part) {
		return (part == null || part.getSize() <=0);
	}
	
	public boolean notBeforThisYear(int year) {
		LocalDate date=LocalDate.now();
		return year> date.getYear();
	}
	
	public boolean isEmail(String string) {
		return string.indexOf('@')!=-1;
	}
	
	public boolean isNull(Object obj) {
		return obj==null;
	}
	
	public String getFileExtension(String fileName) {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        	return fileName.substring(fileName.lastIndexOf(".")+1);
        else 
        	return "";
    }
}
