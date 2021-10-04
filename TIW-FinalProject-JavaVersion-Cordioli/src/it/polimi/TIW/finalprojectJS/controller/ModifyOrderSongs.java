package it.polimi.TIW.finalprojectJS.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.TIW.finalprojectJS.beans.Playlist;
import it.polimi.TIW.finalprojectJS.beans.SongOrder;
import it.polimi.TIW.finalprojectJS.beans.Song;
import it.polimi.TIW.finalprojectJS.beans.User;
import it.polimi.TIW.finalprojectJS.dao.PlaylistDAO;
import it.polimi.TIW.finalprojectJS.dao.PlaylistSongDAO;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ConnectionManager;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;

/**
 * Servlet implementation class ModifyOrderSongs
 */
@WebServlet("/ModifyOrderSongs")
@MultipartConfig
public class ModifyOrderSongs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection conn;
	public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonString = request.getParameter("json");
		String idPlaylistString = request.getParameter("idPlaylist");
		Integer idPlaylist=-1;
		//System.out.println(jsonString);
		Gson gson = new Gson();
		 
		SongOrder[] songs = gson.fromJson(jsonString, SongOrder[].class);
		/*
		for(int i=0 ; i<songs.length; i++) {
			System.out.println("SongID: "+songs[i].getSongId()+" order: "+songs[i].getSongOrder());
		}
		*/
		HttpSession session=request.getSession(false);  
		User user=null;
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			user = (User)session.getAttribute("user");
		}
		//check playlist!
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
		
		//fare il get delle canzoni nella playlist e controllare gli id
		List<Song> songInRealPlaylist =null;
		try {
			songInRealPlaylist=playlistDAO.getAllSongOfPlaylist(idPlaylist, false);
		}catch(Exception e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed query get song playlist sort!", response);
			return;
		}
		boolean find;
		for(int i=0; i<songInRealPlaylist.size(); i++) {
			find=false;
			for(int j=0 ; j<songs.length; j++) {
				if(songInRealPlaylist.get(i).getIdSong()==songs[j].getSongId()) {
					find=true;
					break;
				}
			}
			if(!find) {
				ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Error in Song Id, tring to modify song not in playlist!", response);
				return;
			}
		}
		
		
		try {
			playlistDAO.updatePlaylistOrdered(idPlaylist);
		}
		catch(SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed query update playlist sort!", response);
			return;
		}
		
		PlaylistSongDAO playlistsongDAO= new PlaylistSongDAO(conn);
		try {
			for(int i=0 ; i<songs.length; i++) {
				System.out.println("SongID: "+songs[i].getSongId()+" order: "+songs[i].getSongOrder());
				playlistsongDAO.updateSongOrder(songs[i].getSongId(), songs[i].getSongOrder(), idPlaylist);
			}
		}catch(SQLException e){
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed query update song order!", response);
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
