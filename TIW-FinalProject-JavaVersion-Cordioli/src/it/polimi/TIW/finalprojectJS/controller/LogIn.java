package it.polimi.TIW.finalprojectJS.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.TIW.finalprojectJS.beans.User;
import it.polimi.TIW.finalprojectJS.dao.UserDAO;
import it.polimi.TIW.finalprojectJS.utility.CheckVariable;
import it.polimi.TIW.finalprojectJS.utility.ErrorManager;
import it.polimi.TIW.finalprojectJS.utility.ConnectionManager;



@WebServlet("/LogIn")
@MultipartConfig
public class LogIn extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	Connection conn;
	public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        conn=ConnectionManager.instance.connectToDb(context);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("CIAO LOLLO");
		String email=null;
		String password=null;
		email=request.getParameter("emailLogIn");
		password=request.getParameter("passwordLogIn");
		
		System.out.println("email: "+email+" password: "+password);
		if(CheckVariable.instance.badFormatString(email) || CheckVariable.instance.badFormatString(password)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing email or password!", response);
			return;
		}
		
		
		UserDAO userDAO = new UserDAO(conn);
		int idUtente=-1;
		try {
			idUtente=userDAO.existUserWith(email, password);
		}catch(Exception e) {
			//e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue finding your account on db", response);
			return;
		}
		
		if(idUtente!=-1) {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			String name="";
			try {
				User user = userDAO.getUserWithId(idUtente);
				request.getSession().setAttribute("user", user);
				name = user.getName();
			} catch (SQLException e) {
				//will  nevar happen we are sure idUser id a correct one
				e.printStackTrace();
			}
			response.getWriter().println(name);
		}
		else {
			ErrorManager.instance.setError(HttpServletResponse.SC_UNAUTHORIZED, "Issue finding your account on db!", response);
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
