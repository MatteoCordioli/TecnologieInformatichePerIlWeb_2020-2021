package it.polimi.TIW.finalproject.controller;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import it.polimi.TIW.finalproject.utility.ConnectionManager;
import it.polimi.TIW.finalproject.utility.ErrorManager;

/**
 * Servlet implementation class Error
 */
@WebServlet("/Error")
public class Error extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private TemplateEngine templateEngine;
    
    public void init() throws ServletException{	
    	ServletContext context = getServletContext();
        this.templateEngine=ConnectionManager.instance.connectToTemplate(context);
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "Templates/error.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("code", ErrorManager.instance.getErrorCode());
		ctx.setVariable("message", ErrorManager.instance.getErrorString());
		ErrorManager.instance.eraseErrorInfo();
		templateEngine.process(path, ctx, response.getWriter());
	}

	
}
