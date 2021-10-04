package it.polimi.TIW.finalproject.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class PrecGroupOfSongs
 */
@WebServlet("/PrecGroupOfSongs")
public class PrecGroupOfSongs extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		Integer groupToShow=-1;
		if(CheckVariable.instance.isNull(session)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid session! Please re-login.", response);
			return;
		}
		else {
			groupToShow=(Integer)session.getAttribute("groupToShow");
			session.setAttribute("groupToShow", groupToShow-1);
		}
		response.sendRedirect("Playlist?idPlaylist="+session.getAttribute("idPlaylist"));
	}

}
