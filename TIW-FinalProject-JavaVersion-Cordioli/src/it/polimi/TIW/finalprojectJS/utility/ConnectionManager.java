package it.polimi.TIW.finalprojectJS.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

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
	
	public void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

}
