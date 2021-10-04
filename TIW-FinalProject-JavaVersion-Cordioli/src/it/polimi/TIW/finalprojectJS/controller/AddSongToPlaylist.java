package it.polimi.TIW.finalprojectJS.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.TIW.finalprojectJS.beans.Song;
import it.polimi.TIW.finalprojectJS.dao.SongDAO;
import it.polimi.TIW.finalprojectJS.dao.PlaylistSongDAO;
import it.polimi.TIW.finalprojectJS.beans.Playlist;
import it.polimi.TIW.finalprojectJS.beans.User;
import it.polimi.TIW.finalprojectJS.dao.PlaylistDAO;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ConnectionManager;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;

/**
 * Servlet implementation class AddSongToPlaylist
 */
@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn;
	public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user=null;
		String idSongString=request.getParameter("idSong");
		String idPlaylistString=request.getParameter("idPlaylist");
		Integer idSong=-1;
		Integer idPlaylist=-1;
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			user = (User)session.getAttribute("user");
		}
		
		//controlli playlist id e controllo tutto sulla playlist
		if (idPlaylistString == null || idPlaylistString.isEmpty()) {	//TODO: check the right function in the error manager
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
		if(playlist.getIdUser()!=user.getId()) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The playlist u tried to access it's not yours!", response);
			return;
		}
		
		//controlli song di e controllo tutto sulla song
		if (idSongString == null || idSongString.isEmpty()) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter:idSong!", response);
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
		Song song = new Song();
		try {
			song = songDAO.getSongWithId(idSong);
		}catch(SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed query get song", response);
			return;
		}
		if(CheckVariable.instance.isNull(song)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The song u tried to access does not exist!", response);
			return;
		}
		if(song.getAlbum().getIdUser()!=user.getId()) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The song u tried to access it's not yours!", response);
			return;
		}
		
		PlaylistSongDAO  playlistSongDAO= new PlaylistSongDAO(conn);
		try {
			playlistSongDAO.newLinkSongPlaylist(idSong, idPlaylist);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue adding your song to playlist!", response);
			return;
		}
	}
	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
