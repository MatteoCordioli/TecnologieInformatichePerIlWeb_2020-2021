package it.polimi.TIW.finalprojectJS.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.TIW.finalprojectJS.beans.Song;

public class SongDAO {
	private Connection con;
	
	/**
	 * Build a userDAO with the connection i'm using in the servlet
	 * @param connection the connection i want this DAO to use*/
	public SongDAO(Connection connection) {
		this.con = connection;
	}
	
	/**
	 * Find the Song with a specific id
	 * @param idSong: the id i want to find
	 * @throws sqlException if i have problem with the interaction with the db
	 * @return the song with that id
	 * @throws Exception */
	public Song getSongWithId(int idSong) throws SQLException {
		String query = "SELECT * FROM song WHERE idSong=?";
		Song song=null;
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idSong);
			result = pstatement.executeQuery();
			//TODO: penso di spossa togliere il while e lasciare le istruzioni senza il ciclo, sono certo ci sarà sempre 1 sola riga
			while (result.next()) {
				song=new Song();
				song.setIdSong(idSong);
				song.setTitle(result.getString("title"));
				song.setGenre(result.getString("genre"));
				song.setSongPath(result.getString("song"));
				song.setIdAlbum(result.getInt("idAlbum"), con);
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
		return song;
	}
	
	/**
	 * Insert a new row in db
	 * @param title the title to insert
	 * @param idAlbum the album to insert 
	 * @param genre the genre to insert
	 * @param songPath the path to insert
	 * @throws sqlException if the query does not work*/
	public void newSong(String title, int idAlbum, String genre, String songPath) throws SQLException {
		String query = "INSERT into playlistmusicalecordioli.song (title, idAlbum, genre, song)   VALUES(?, ?, ?, ?)";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, title);
			pstatement.setInt(2, idAlbum);
			pstatement.setString(3, genre);
			pstatement.setString(4, songPath);
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
