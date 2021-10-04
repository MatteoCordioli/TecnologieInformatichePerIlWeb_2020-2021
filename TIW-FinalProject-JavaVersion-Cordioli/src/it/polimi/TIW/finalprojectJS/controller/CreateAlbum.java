package it.polimi.TIW.finalprojectJS.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import it.polimi.TIW.finalprojectJS.beans.User;
import it.polimi.TIW.finalprojectJS.dao.AlbumDAO;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ConnectionManager;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;

/**
 * Servlet implementation class CreateAlbum
 */
@WebServlet("/CreateAlbum")
@MultipartConfig
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn = null;
	String folderPath = "";
	public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
        folderPath = context.getInitParameter("outputpath");
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("AlbumTitle");
		String singer = request.getParameter("AlbumSinger");
		String yearString = request.getParameter("AlbumYear");
		Part filePartCover = request.getPart("AlbumCover");
		int year=-1;
		
		HttpSession session=request.getSession(false);  
		User user=null;
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			user = (User)session.getAttribute("user");
		}
		
		//check strings are valid
		if(CheckVariable.instance.badFormatString(title) || CheckVariable.instance.badFormatString(singer) || CheckVariable.instance.badFormatString(yearString)){
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing data in request!", response);
			return;
		}
		else {
			try {
				year = Integer.parseInt(yearString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Bad format year!", response);
				return;
			}
			if(year<1900 || year>2100) {
				ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Bad format year!", response);
				return;
			}
		}
		
		//check file is valid
		if (CheckVariable.instance.badFormatFile(filePartCover)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!", response);
			return;
		}
		String contentType = filePartCover.getContentType();
		System.out.println("Type " + contentType);
		if (!contentType.startsWith("image")) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted, u have to insert an image!", response);
			return;
		}
		
		//Saving the cover
		String fileName = Paths.get(filePartCover.getSubmittedFileName()).getFileName().toString();
		System.out.println("Filename: " + fileName);
		String extension= CheckVariable.instance.getFileExtension(fileName);
		fileName="CoverOf_"+title+"_OfUser"+user.getId()+"_Rand_";
		Random rand= new Random();
		for(int i=0; i<5; i++) {
			fileName += ((Integer)rand.nextInt(9)).toString();
		}
		fileName+="."+extension;
		
		String outputPath = folderPath + user.getId()+ "\\" +fileName; //folderPath inizialized in init
		System.out.println("Output path: " + outputPath);
		
		File directory = new File(folderPath + user.getId()+ "\\");
		if(!directory.exists()) {
			directory.mkdir();
		}
			
		File newFile = new File(outputPath);

		try (InputStream fileContent = filePartCover.getInputStream()) {
			Files.copy(fileContent, newFile.toPath());
			System.out.println("Cover saved correctly!");	
			
		} catch (Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving cover", response);
			return;
		}
		
		//save the info in the db
		
		AlbumDAO albumDAO=new AlbumDAO(conn);
		try {
			albumDAO.newAlbum(user.getId(), title, singer, year, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue adding your album!", response);
			return;
		}
		
		System.out.println("AlbumAggiunto");
	}

	
	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
