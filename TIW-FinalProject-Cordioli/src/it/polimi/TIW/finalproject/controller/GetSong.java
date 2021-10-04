package it.polimi.TIW.finalproject.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class GetSong
 */
@WebServlet("/GetSong/*")
public class GetSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	String folderPath = "";

	public void init() throws ServletException {
		// get folder path from webapp init parameters inside web.xml
		folderPath = getServletContext().getInitParameter("outputpath");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo= request.getParameter("songPath");
		//System.out.println("Real path: "+pathInfo);
		
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		
		if(session==null) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "NOT LOGGED IN", response);
		}
		else {
			idUser=(Integer)session.getAttribute("idUser");  
		}
		
		
		if (pathInfo == null) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!", response);
			return;
		}
		
		if (pathInfo == null || pathInfo.equals("/")) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!", response);
			return;
		}

		String filename=pathInfo;
		File file = new File(folderPath +idUser.toString()+"\\", filename); //folderPath inizialized in init
		//System.out.println(folderPath+filename);

		if (!file.exists() || file.isDirectory()) {
			ErrorManager.instance.setError(HttpServletResponse.SC_NOT_FOUND, "File not present", response);
			return;
		}

		// set headers for browser
		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
																									
		// copy file to output stream
		Files.copy(file.toPath(), response.getOutputStream());
	}
}
