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
import it.polimi.TIW.finalproject.beans.Playlist;
import it.polimi.TIW.finalproject.dao.PlaylistDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class Playlist
 */
@WebServlet("/Playlist")
public class PlaylistPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    Connection conn;
    private TemplateEngine templateEngine;
    
    public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        this.templateEngine = ConnectionManager.instance.connectToTemplate(context);
        conn=ConnectionManager.instance.connectToDb(context);
    }
    
	//@SuppressWarnings("null")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		String idPlaylistString=request.getParameter("idPlaylist");
		Integer idPlaylist=-1;
		Integer groupToShow=-1;
		boolean precButton=true;
		boolean nextButton=true;
		
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			try {
				idUser=(Integer)session.getAttribute("idUser");  
				groupToShow=(Integer)session.getAttribute("groupToShow");
			}
			catch(Exception e) {
				ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
				return;
			}
			
		}
		if (idPlaylistString == null || idPlaylistString.isEmpty()) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter:idPlaylist!", response);
			return;
		}
		else {
			try {
				idPlaylist= Integer.parseInt(idPlaylistString);
			}
			catch(Exception e) {
				e.printStackTrace();
				ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Bad format of paramenter: idPlaylist!", response);
				return;
			}
		}
		
		//playlistDAO
		PlaylistDAO playlistDAO=new PlaylistDAO(conn);
		Playlist playlist=new Playlist();
		
		try {
			playlist=playlistDAO.getPlaylist(idPlaylist);
		}
		catch (SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed query get playlist", response);
			return;
		}
		if(CheckVariable.instance.isNull(playlist)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The playlist u tried to access does not exist!", response);
			return;
		}
		if(playlist.getIdUser()!=idUser) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The playlist u tried to access it's not yours!", response);
			return;
		}
		session.setAttribute("idPlaylist", idPlaylist);
		List<Song> songsPlaylist = null;
		List<Song> myFiveSongToShow = new ArrayList<Song>();
		try {
			songsPlaylist=playlistDAO.getAllSongOfPlaylist(idPlaylist);
		}catch (SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed getting song of playlist", response);
			return;
		}
		for(int i=groupToShow*5-5; i<groupToShow*5 && i<songsPlaylist.size();i++) {
			myFiveSongToShow.add(songsPlaylist.get(i));
		}
		
		if(groupToShow==1) {
			precButton=false;
		}
		if(groupToShow*5>=songsPlaylist.size()) {
			nextButton=false;
		}
		List<Song> songsNotInCurrPlaylist = new ArrayList<Song>();
		try {
			songsNotInCurrPlaylist=playlistDAO.getAllSongOfNotIn(idUser,idPlaylist);
			
		} catch (SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed getting songs not in playlist", response);
			return;
		}
		
		String path = "Templates/playlist.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("songsNotInPlaylist", songsNotInCurrPlaylist);
		ctx.setVariable("songInPlaylist", myFiveSongToShow);
		ctx.setVariable("playlist", playlist);
		ctx.setVariable("nextButton", nextButton);
		ctx.setVariable("precButton", precButton);
		
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
