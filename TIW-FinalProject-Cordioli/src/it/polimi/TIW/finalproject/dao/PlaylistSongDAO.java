package it.polimi.TIW.finalproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlaylistSongDAO {
	private Connection con;
	
	/**
	 * Build a userDAO with the connection i'm using in the servlet
	 * @param connection the connection i want this DAO to use*/
	public PlaylistSongDAO(Connection connection) {
		this.con = connection;
	}
	
	
	/*
	 * Insert a new row in db
	 * @param idSong the song to insert
	 * @param idPlayllist the playlist to insert 
	 * @throws sqlException if the query does not work*/
	public void newLinkSongPlaylist(int idSong, int idPlaylist) throws SQLException {
		String query = "INSERT into playlistmusicalecordioli.playlistsong (idSong, idPlaylist)   VALUES(?, ?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idSong);
			pstatement.setInt(2, idPlaylist);
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
