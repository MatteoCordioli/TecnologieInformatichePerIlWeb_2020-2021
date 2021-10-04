package it.polimi.TIW.finalproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.TIW.finalproject.beans.User;

public class UserDAO {
	private Connection con;
	
	/**
	 * Build a userDAO with the connection i'm using in the servlet
	 * @param connection the connection i want this DAO to use*/
	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * Find a user with a specific email and pass
	 * @param email: email i'm searching for
	 * @param pass: the password to enter in you personal area
	 * @throws sqlException if i have problem with the interaction with the db
	 * @return the id of the user that has just logged in, otherwise -1 if there is no user with that credential*/
	public Integer existUserWith(String email, String pass) throws SQLException {
		String query = "SELECT idUser FROM user WHERE email=?  and password=?";
		Integer idUser=-1;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setString(2, pass);
			result = pstatement.executeQuery();
			while (result.next()) {
				idUser = result.getInt("idUser");
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return idUser;
	}
	
	/**
	 * Find the user with a specific id
	 * @param idUser: the id i want to find
	 * @throws sqlException if i have problem with the interaction with the db
	 * @return the user with that id*/
	public User getUserWithId(int idUser) throws SQLException {
		String query = "SELECT * FROM user WHERE idUser=?";
		User user=new User();
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idUser);
			result = pstatement.executeQuery();
			//TODO: penso di spossa togliere il while e lasciare le istruzioni senza il ciclo, sono certo ci sarà sempre 1 sola riga
			while (result.next()) {
				user.setName(result.getString("name"));
				user.setId(idUser);
				user.setEmail(result.getString("email"));
				user.setPassword(result.getString("password"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return user;
	}
	
	/**
	 * Insert a new row in db
	 * @param email the email to insert
	 * @param pass the pass to insert 
	 * @param name the name to insert
	 * @throws sqlException if the query does not work*/
	public void newUser(String email, String pass, String name) throws SQLException {
		String query = "INSERT into playlistmusicalecordioli.user (email, name, password)   VALUES(?, ?, ?)";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, email);
			pstatement.setString(2, name);
			pstatement.setString(3, pass);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {

			}
		}
	}
}
