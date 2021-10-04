package it.polimi.TIW.finalprojectJS.controller;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.TIW.finalprojectJS.utility.ConnectionManager;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;
import it.polimi.TIW.finalprojectJS.beans.Album;
import it.polimi.TIW.finalprojectJS.dao.AlbumDAO;
import it.polimi.TIW.finalprojectJS.beans.User;

/**
 * Servlet implementation class GetPlaylist
 */
@WebServlet("/GetAlbums")
public class GetAlbums extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn;
	public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		User user=null;
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			user = (User)session.getAttribute("user");
		}
		
		AlbumDAO albumDAO = new AlbumDAO(conn);
		List<Album> albums;
		try {
			albums = albumDAO.albumsOfUser(user.getId());
			
		} catch (SQLException e) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed", response);
			return;
		}
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
	
		String json = gson.toJson(albums);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		System.out.println("Get degli album");
	}

	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
