package it.polimi.TIW.finalproject.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import it.polimi.TIW.finalproject.beans.Album;
import it.polimi.TIW.finalproject.beans.Playlist;
import it.polimi.TIW.finalproject.beans.User;
import it.polimi.TIW.finalproject.dao.AlbumDAO;
import it.polimi.TIW.finalproject.dao.PlaylistDAO;
import it.polimi.TIW.finalproject.dao.UserDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;



/**
 * Servlet implementation class HomePage
 */
@WebServlet("/HomePage")
public class HomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	Connection conn;
    private TemplateEngine templateEngine;
    
    
    public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        this.templateEngine = ConnectionManager.instance.connectToTemplate(context);
        conn=ConnectionManager.instance.connectToDb(context);
    }
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		boolean newSongCreated=false;
		boolean newAlbumCreated=false;
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			try {
				idUser=(Integer)session.getAttribute("idUser"); 
			}
			catch(Exception e) {
				ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
				return;
			}
			session.setAttribute("groupToShow", 1);
			if(!CheckVariable.instance.isNull(session.getAttribute("newSong"))) {
				newSongCreated=(boolean)session.getAttribute("newSong");
			}
			if(!CheckVariable.instance.isNull(session.getAttribute("newAlbum"))) {
				newAlbumCreated=(boolean)session.getAttribute("newAlbum");
			}
		}
		
		
		//VARIABILE PER IL NOME
		UserDAO userDAO= new UserDAO(conn);
		User user;
		
		//VARIABILE PER PLAYLIST
		PlaylistDAO playlistDAO = new PlaylistDAO(conn);
		List<Playlist> playlists;
		
		//VARIABILE PER ALBUM
		AlbumDAO albumDAO = new AlbumDAO(conn);
		List<Album> albums;
		
		//CHIAMO IL TEMPLATE E I DAO
		try {
			user = userDAO.getUserWithId(idUser);
			playlists = playlistDAO.playlistOfUser(idUser);
			albums=albumDAO.albumsOfUser(idUser);
			
		} catch (SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed", response);
			return;
		}
		
		String path = "Templates/home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("playlists", playlists);
		ctx.setVariable("currUser", user);
		ctx.setVariable("albums", albums);
		ctx.setVariable("newSongCreated", newSongCreated);
		ctx.setVariable("newAlbumCreated", newAlbumCreated);
		session.setAttribute("newAlbum", false);
		session.setAttribute("newSong", false);
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
