package it.polimi.TIW.finalprojectJS.controller;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.TIW.finalprojectJS.beans.Song;
import it.polimi.TIW.finalprojectJS.beans.Playlist;
import it.polimi.TIW.finalprojectJS.dao.PlaylistDAO;
import it.polimi.TIW.finalprojectJS.beans.User;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ConnectionManager;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;

/**
 * Servlet implementation class GetSongsNotIn
 */
@WebServlet("/GetSongsNotIn")
public class GetSongsNotIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn;
	public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User user=null;
		String idPlaylistString=request.getParameter("idPlaylist");
		Integer idPlaylist=-1;
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			user = (User)session.getAttribute("user");
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
		List<Song> songsNotInCurrPlaylist = new ArrayList<Song>();
		try {
			songsNotInCurrPlaylist=playlistDAO.getAllSongOfNotIn(user.getId(),idPlaylist);
			
		} catch (SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed getting songs not in playlist", response);
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(songsNotInCurrPlaylist);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		System.out.println("Get delle song not in");
		
	}
	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
