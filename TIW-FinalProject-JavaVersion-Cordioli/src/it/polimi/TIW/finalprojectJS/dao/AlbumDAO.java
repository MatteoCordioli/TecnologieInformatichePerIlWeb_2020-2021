package it.polimi.TIW.finalprojectJS.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import it.polimi.TIW.finalprojectJS.beans.Album;

public class AlbumDAO {
	private Connection con;

	public AlbumDAO(Connection con) {
		super();
		this.con = con;
	}

	/**
	 * Insert a new row in db
	 * @param idUser the user that own this album
	 * @param nameAlbum the string to assign to the name in db
	 * @param nameSinger the string to assign to the singer in db
	 * @param yearPup the int that corrispond to the year of publication
	 * @param coverPath the string that contain the path where we have saved the image server side
	 * @throws sqlException if the query does not work*/
	public void newAlbum(int idUser, String nameAlbum, String nameSinger, int yearPub, String coverPath) throws SQLException {
		String query = "INSERT into playlistmusicalecordioli.album (idUser, nameAlbum, nameSinger, yearPublication, image)   VALUES(?, ?, ?, ?, ?)";

		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idUser);
			pstatement.setString(2, nameAlbum);
			pstatement.setString(3, nameSinger);
			pstatement.setInt(4, yearPub);
			pstatement.setString(5, coverPath);
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
	
	/**
	 * method used to get all the albums of a single user
	 * @param id the id of the user i want to examinate
	 * @return a list of the album i'm interested in
	 * @throws sqlException if failed to access db*/
	public List<Album> albumsOfUser(int idUser) throws SQLException{
		List<Album> albums = new ArrayList<Album>();
		String query="SELECT * FROM album WHERE idUser = ? ORDER BY nameAlbum";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idUser);
			result = pstatement.executeQuery();
			while (result.next()) {
				Album album = new Album();
				album.setIdAlbum(result.getInt("idAlbum"));
				album.setIdUser(result.getInt("idUser"));
				album.setNameAlbum(result.getString("nameAlbum"));
				album.setNameSinger(result.getString("nameSinger"));
				album.setYearPublication(result.getInt("yearPublication"));
				album.setImagePath(result.getString("image"));
				albums.add(album);
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
		return albums;
	}
	
	/**
	 * asking for a specific album
	 * @param idAlbum: the id of the album i want to get
	 * @return an Album obj with all the info about it
	 * @throws sqlException if failed to access db*/
	public Album getAlbumWithId(int idAlbum) throws SQLException {
		String query ="SELECT * FROM album WHERE idAlbum = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		Album album = new Album();
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idAlbum);
			result = pstatement.executeQuery();
			//TODO: penso di spossa togliere il while e lasciare le istruzioni senza il ciclo, sono certo ci sarà sempre 1 sola riga
			while (result.next()) {
				album.setIdUser(result.getInt("idUser"));
				album.setNameAlbum(result.getString("nameAlbum"));
				album.setNameSinger(result.getString("nameSinger"));
				album.setYearPublication(result.getInt("yearPublication"));
				album.setImagePath(result.getString("image"));
			}
		}catch (SQLException e) {
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
		
		return album;
	}
}
