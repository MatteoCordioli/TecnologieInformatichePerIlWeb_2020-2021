package it.polimi.TIW.finalproject.controller;

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

import it.polimi.TIW.finalproject.beans.Album;
import it.polimi.TIW.finalproject.dao.AlbumDAO;
import it.polimi.TIW.finalproject.dao.SongDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class CreateSong
 */
@WebServlet("/CreateSong")
@MultipartConfig
public class CreateSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn = null;
	String folderPath = "";
	public void init() throws ServletException {

		//db connection
		ServletContext context = getServletContext();
		conn=ConnectionManager.instance.connectToDb(context);
		folderPath = context.getInitParameter("outputpath");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("SongTitle");
		String genre = request.getParameter("SongGenre");
		String idAlbumString = request.getParameter("AlbumTitleSelect");
		Part filePartSong = request.getPart("SongMusic");
		Integer idAlbum=-1;
		
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			idUser=(Integer)session.getAttribute("idUser");  
		}
		
		//check strings are valid
		if(CheckVariable.instance.badFormatString(title) || CheckVariable.instance.badFormatString(genre) || CheckVariable.instance.badFormatString(idAlbumString)){
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters in request!", response);
			return;
		}else {
			try {
				idAlbum = Integer.parseInt(idAlbumString);		
			} catch (NumberFormatException e) {
				e.printStackTrace();
				ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Bad format idAlbum!", response);
				return;
			}
		}
	
		AlbumDAO albumDAO= new AlbumDAO(conn);
		Album album;
		try {
			album = albumDAO.getAlbumWithId(idAlbum);
		} catch (SQLException e1) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue find album selected!", response);
			return;
		}
		
		if(album.getIdUser()!=idUser) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Album selected is not yours!", response);
			return;
		}
		
		
		//check file is valid
		if (CheckVariable.instance.badFormatFile(filePartSong)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!", response);
			return;
		}
		String contentType = filePartSong.getContentType();
		//System.out.println("Type " + contentType);
		if (!contentType.startsWith("audio")) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "File format not permitted, u have not inserted an audio!", response);
			return;
		}
		
		//Saving the cover
		String fileName = Paths.get(filePartSong.getSubmittedFileName()).getFileName().toString();
		//System.out.println("Filename: " + fileName);
		String extension= CheckVariable.instance.getFileExtension(fileName);
		fileName="Song_"+title+"_OfUser"+idUser+"_Rand_";
		Random rand= new Random();
		for(int i=0; i<5; i++) {
			fileName += ((Integer)rand.nextInt(9)).toString();
		}
		fileName+="."+extension;
		
		
		String outputPath = folderPath + idUser+ "\\" +fileName; //folderPath inizialized in init
		System.out.println("Output path: " + outputPath);
		
		File directory = new File(folderPath + idUser+ "\\");
		if(!directory.exists()) {
			directory.mkdir();
		}
			
		
		File newFile = new File(outputPath);

		try (InputStream fileContent = filePartSong.getInputStream()) {
			Files.copy(fileContent, newFile.toPath());
			System.out.println("Song saved correctly!");	
			
		} catch (Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving song file!", response);
			return;
		}
		
		
		//save the info in the db
		SongDAO songDAO=new SongDAO(conn);
		try {
			songDAO.newSong(title, idAlbum, genre, fileName);
			//albumDAO.newAlbum(idUser, title, singer, year, outputPath);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue adding your song on db!", response);
			return;
			
		}
		session.setAttribute("newSong", true);
		response.sendRedirect("HomePage");
	}
	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
