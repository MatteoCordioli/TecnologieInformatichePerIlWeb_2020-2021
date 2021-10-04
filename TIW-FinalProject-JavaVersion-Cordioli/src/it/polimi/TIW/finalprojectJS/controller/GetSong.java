package it.polimi.TIW.finalprojectJS.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.TIW.finalprojectJS.beans.User;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;



/**
 * Servlet implementation class GetCover
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
		
		HttpSession session = request.getSession(false);
		User user=null;
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			user = (User)session.getAttribute("user");
		}
		
		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!");
			return;
		}

		String filename=pathInfo;
		File file = new File(folderPath +user.getId()+"\\", filename); //folderPath inizialized in init
		//System.out.println(folderPath+filename);

		if (!file.exists() || file.isDirectory()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not present");
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
