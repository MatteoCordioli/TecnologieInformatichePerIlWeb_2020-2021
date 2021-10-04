package it.polimi.TIW.finalproject.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class ConnectionManager {
	public static ConnectionManager instance=new ConnectionManager();
	
	/*
	public static ConnectionManager getInstance() {
		return instance;
	}
	*/
	public Connection connectToDb(ServletContext context) throws UnavailableException {
		Connection conn;
		try {
 			String driver = context.getInitParameter("dbDriver");
 			String url = context.getInitParameter("dbUrl");
 			String user = context.getInitParameter("dbUser");
 			String password = context.getInitParameter("dbPassword");
 			Class.forName(driver);
 			conn = DriverManager.getConnection(url, user, password);

 		} catch (ClassNotFoundException e) {
 			e.printStackTrace();
 			throw new UnavailableException("Can't load database driver[LOGIN]");
 		} catch (SQLException e) {
 			e.printStackTrace();
 			throw new UnavailableException("Couldn't get db connection[LOGIN]");
 		}
		return conn;
	}
	
	public TemplateEngine connectToTemplate(ServletContext context) {
		TemplateEngine tempEngine = new TemplateEngine();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
        tempEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        return tempEngine;
	}
	public void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
	
}
