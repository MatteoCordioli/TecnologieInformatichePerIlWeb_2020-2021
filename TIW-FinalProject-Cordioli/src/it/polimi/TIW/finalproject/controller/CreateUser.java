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

import it.polimi.TIW.finalproject.dao.UserDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class CreateUser
 */
@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Connection conn = null;

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		conn=ConnectionManager.instance.connectToDb(context);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("username");
		String email = request.getParameter("useremail");
		String password1 = request.getParameter("userpswd1");
		String password2 = request.getParameter("userpswd2");
		
		if(	CheckVariable.instance.badFormatString(name) || CheckVariable.instance.badFormatString(email) ||
			CheckVariable.instance.badFormatString(password1) || CheckVariable.instance.badFormatString(password2)){
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters", response);
			return;
		}
				
		if(!CheckVariable.instance.isEmail(email)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "You have to enter an email!", response);
			return;
		}
		if(!password1.equals(password2)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Enter the same password!", response);
			return;
		}
		
		UserDAO userDAO = new UserDAO(conn);
		int idUtente=-1;
		try {
			userDAO.newUser(email, password2, name);
			idUtente=userDAO.existUserWith(email, password2);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue adding your account!", response);
			return;
		}
		HttpSession session=request.getSession(true);  
        session.setAttribute("idUser",idUtente);
        session.setAttribute("newUserCreated", true);
        response.sendRedirect("Login");
	}
	public void destroy() {
		try {
			ConnectionManager.instance.closeConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
