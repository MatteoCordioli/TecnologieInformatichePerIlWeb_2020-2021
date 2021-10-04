package it.polimi.TIW.finalproject.controller;

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

import it.polimi.TIW.finalproject.beans.Song;
import it.polimi.TIW.finalproject.dao.PlaylistSongDAO;
import it.polimi.TIW.finalproject.dao.SongDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class AddSongToPlaylist
 */
@WebServlet("/AddSongToPlaylist")
public class AddSongToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn = null;

	public void init() throws ServletException {
		ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		String idSongString=request.getParameter("songid");
		Integer idPlaylist=-1;
		Integer idSong=-1;
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			idUser=(Integer)session.getAttribute("idUser"); 
			idPlaylist=(Integer)session.getAttribute("idPlaylist");
		}
		
		
		//check idSong valid
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
		if(song.getAlbum().getIdUser()!=idUser) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "The song u tried to access it's not yours!", response);
			return;
		}
		//System.out.println(idSong+"-"+idPlaylist);
		PlaylistSongDAO  playlistSongDAO= new PlaylistSongDAO(conn);
		try {
			playlistSongDAO.newLinkSongPlaylist(idSong, idPlaylist);
		} catch (Exception e) {
			//e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue adding your song to playlist!", response);
			return;
		}
		response.sendRedirect("Playlist?idPlaylist="+idPlaylist);
	}
	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
