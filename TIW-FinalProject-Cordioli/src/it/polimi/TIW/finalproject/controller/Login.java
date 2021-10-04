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

import org.thymeleaf.TemplateEngine;
import it.polimi.TIW.finalproject.beans.User;
import it.polimi.TIW.finalproject.dao.UserDAO;
import it.polimi.TIW.finalproject.utility.CheckVariable;
import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

import org.thymeleaf.context.WebContext;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	Connection conn;
    private TemplateEngine templateEngine;
    
    
    public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        this.templateEngine = ConnectionManager.instance.connectToTemplate(context);
        conn=ConnectionManager.instance.connectToDb(context);
    }
	
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*Guardo se ho una sessione aperta con un id di un utente se lo ho gli metto già dentro i suoi dati por il login*/
		//prendo id inavlido sessione creo nuova sessione
		HttpSession session=request.getSession(true);  
		Integer idUser=(Integer)session.getAttribute("idUser");
		boolean newUserCreated=false;
		boolean logInError=false;
		User user;
		if(CheckVariable.instance.isNull(idUser)) {
			user=new User();
			if(session.getAttribute("errorLogIn")!= null) {
				logInError=(boolean)session.getAttribute("errorLogIn");
				if(logInError) {
					session.setAttribute("errorLogIn", false);
				}
			}
			
		}
		else {
			if(!CheckVariable.instance.isNull(session.getAttribute("newUserCreated"))) {
				newUserCreated=(boolean)session.getAttribute("newUserCreated");
				if(newUserCreated) {
					session.setAttribute("newUserCreated", false);
				}
			}
			UserDAO userDAO = new UserDAO(conn);
			try {
				user=userDAO.getUserWithId(idUser);
			}
			catch(Exception e){
				ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue finding you account in session!", response);
				return;
			}
			session.invalidate();//se refreshi 1 volta quando sei usicto perdi i dati di default
		}
		//System.out.println(newUserCreated);
		String path = "index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("currUser", user);
		ctx.setVariable("newUserCreated", newUserCreated);
		ctx.setVariable("logInError", logInError);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * validate the log in, if it is not correct just forward on himself
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("emailLogIn");
		String password = request.getParameter("passwordLogIn");
        
		
		if(CheckVariable.instance.badFormatString(email) || CheckVariable.instance.badFormatString(password)) {
			ErrorManager.instance.setError(HttpServletResponse.SC_BAD_REQUEST, "Missing email or password!", response);
			return;
		}
		
		UserDAO userDao= new UserDAO(conn);
		int idUtente=-1;
		try {
			idUtente=userDao.existUserWith(email, password);
		}catch(Exception e) {
			e.printStackTrace();
			ErrorManager.instance.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue finding your account on db", response);
			return;
		}
		
		if(idUtente!=-1) {
			//response.sendRedirect("la servlet per setuppare la pagina 1");
			
			//response.sendRedirect("HomePageServlet?id="+idUtente);
			
			HttpSession session=request.getSession(true);  
	        session.setAttribute("idUser",idUtente);
	        response.sendRedirect("HomePage");
		}
		else {
			HttpSession session=request.getSession(false);
			session.setAttribute("errorLogIn", true);
			doGet(request, response);
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
