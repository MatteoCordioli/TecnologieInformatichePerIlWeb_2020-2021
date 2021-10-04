package it.polimi.TIW.finalproject.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import it.polimi.TIW.finalproject.beans.Song;
import it.polimi.TIW.finalproject.dao.PlaylistDAO;
import it.polimi.TIW.finalproject.dao.SongDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class Player
 */
@WebServlet("/Player")
public class Player extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	Connection conn;
    private TemplateEngine templateEngine;
    
    public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        this.templateEngine=ConnectionManager.instance.connectToTemplate(context);
        conn=ConnectionManager.instance.connectToDb(context);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		Integer idPlaylist=-1;
		String idSongString=request.getParameter("idSong");
		Integer idSong=-1;
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			try {
				idUser=(Integer)session.getAttribute("idUser"); 
				idPlaylist=(Integer)session.getAttribute("idPlaylist");
			}
			catch(Exception e) {
				ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
				return;
			}
		}
		if (CheckVariable.instance.badFormatString(idSongString)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: idSong!", response);
			return;
		}
		else {
			try {
				idSong= Integer.parseInt(idSongString);
			}
			catch(Exception e) {
				e.printStackTrace();
				ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Bad format of paramenter: idSong!", response);
				return;
			}
		}
		
		SongDAO songDAO = new SongDAO(conn);
		Song song=null;
		try {
			song=songDAO.getSongWithId(idSong);
		}
		catch(SQLException e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed query get song!", response);
			return;
		}
		
		if(CheckVariable.instance.isNull(song)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The song u tried to access does not exist!", response);
			return;
		}
		if(song.getAlbum().getIdUser()!=idUser) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The song u tried to access it's not yours!", response);
			return;
		}
		
		PlaylistDAO playlistDAO = new PlaylistDAO(conn);
		List<Song> songs = new ArrayList<Song>();
		try {
			songs = playlistDAO.getAllSongOfPlaylist(idPlaylist);
		}
		catch(SQLException e){
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed getting song of playlist.", response);
			return;
		}
		
		//controllare che la song scelta sia veramente della playlist in cui ero prima (che è salvata nella session) 
		if(!songs.contains(song)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The song u tried to access is not linked to the current playlist!", response);
			return;
		}
		
		
		String path = "Templates/player.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("song", song);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
