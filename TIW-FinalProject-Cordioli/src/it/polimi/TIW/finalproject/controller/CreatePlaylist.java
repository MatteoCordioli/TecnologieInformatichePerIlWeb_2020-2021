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

import it.polimi.TIW.finalproject.dao.PlaylistDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;


/**
 * Servlet implementation class CreatePlaylist
 */
@WebServlet("/CreatePlaylist")
public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn = null;

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		conn=ConnectionManager.instance.connectToDb(context);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("playlistname");
		HttpSession session=request.getSession(false);  
		Integer idUser=-1;
		
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			idUser=(Integer)session.getAttribute("idUser");
		}

		if (CheckVariable.instance.badFormatString(name)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter.", response);
			return;
		}

		PlaylistDAO playlistDAO = new PlaylistDAO(conn);

		try {
			playlistDAO.newPlaylist(idUser, name);
		} catch (Exception e) {
			//e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue adding your playlist!", response);
			return;
		}
		
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
