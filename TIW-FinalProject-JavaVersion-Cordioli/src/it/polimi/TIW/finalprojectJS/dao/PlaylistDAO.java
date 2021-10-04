package it.polimi.TIW.finalprojectJS.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.TIW.finalprojectJS.beans.Playlist;
import it.polimi.TIW.finalprojectJS.beans.Song;

public class PlaylistDAO {
	private Connection con;

	public PlaylistDAO(Connection con) {
		super();
		this.con = con;
	}
	
	/**
	 * method used to get all the playlist of a single user
	 * @param id the id of the user i want to examinate
	 * @return a list of the playlist i'm interested in
	 * @throws sqlException if failed to access db*/
	public List<Playlist> playlistOfUser(int id) throws SQLException{
		List<Playlist> playlists = new ArrayList<Playlist>();
		String query = "SELECT * FROM playlist WHERE idUser = ? ORDER BY dataCreation DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			while (result.next()) {
				Playlist playlist = new Playlist();
				playlist.setIdPlaylist(result.getInt("idPlaylist"));
				playlist.setNamePlaylist(result.getString("namePlaylist"));
				playlist.setDate(result.getDate("dataCreation").toString());
				playlists.add(playlist);
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
		return playlists;
	}
	
	/**
	 * Insert a new row in db
	 * @param name of the playlist to insert
	 * @throws sqlException if the query does not work*/
	public void newPlaylist(int idUser, String name) throws SQLException {
		String query = "INSERT into playlistmusicalecordioli.playlist (idUser, namePlaylist, dataCreation)   VALUES(?, ?, now())";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idUser);
			pstatement.setString(2, name);
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
	 * Get a specified playlist
	 * @param idPlaylist i want to get
	 * @throws SQLExeption if we have some problem with db */
	public Playlist getPlaylist(int idPlaylist) throws SQLException {
		Playlist playlist=null;
		String query = "SELECT * FROM playlist WHERE idPlaylist = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {	
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idPlaylist);
			result = pstatement.executeQuery();
			while (result.next()) {
				playlist=new Playlist();
				playlist.setIdPlaylist(result.getInt("idPlaylist"));
				playlist.setIdUser(result.getInt("idUser"));
				playlist.setNamePlaylist(result.getString("namePlaylist"));
				playlist.setOrderedByUser(result.getInt("orderedByUser"));
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
		return playlist;
	}
	
	/**
	 * Get all the song stored in a specific playlist
	 * @param idPlaylist i'm interested in
	 * @throws SQLException if has some problems with db access*/
	public List<Song> getAllSongOfPlaylist(int idPlaylist, boolean normalQuery) throws SQLException{
		String query;
		if(normalQuery)
			query = "SELECT song.idSong, song.title, song.idAlbum, song.genre, song.song, playlistsong.customOrder FROM playlistsong JOIN song ON song.idSong=playlistsong.idSong JOIN album ON song.idAlbum=album.idAlbum WHERE idPlaylist=? ORDER BY album.yearPublication DESC";
		else
			query = "SELECT song.idSong, song.title, song.idAlbum, song.genre, song.song, playlistsong.customOrder FROM playlistsong JOIN song ON song.idSong=playlistsong.idSong JOIN album ON song.idAlbum=album.idAlbum WHERE idPlaylist=? ORDER BY playlistsong.customOrder ";

		List<Song> songs=new ArrayList<Song>();
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idPlaylist);
			result = pstatement.executeQuery();
			while (result.next()) {
				Song song = new Song();
				song.setIdSong(result.getInt("idSong"));
				song.setTitle(result.getString("title"));
				song.setIdAlbum(result.getInt("idAlbum"), con);
				song.setGenre(result.getString("genre"));
				song.setSongPath(result.getString("song"));
				song.setCustomOrder(result.getInt("customOrder"));
				//song.setAlbum(result.getString("nameAlbum"), result.getString("nameSinger"), result.getString("image"));
				songs.add(song);
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
		return songs;
	}
	
	/**
	 * Get all the song not stored in a specific playlist
	 * @param idUser the user i'm looking at
	 * @param idPlaylist i'm interested in
	 * @throws SQLException if has some problems with db access*/
	public List<Song> getAllSongOfNotIn(int idUser, int idPlaylist) throws SQLException {
		String query = "SELECT idSong, song.title, album.idAlbum FROM song JOIN album ON song.idAlbum=album.idAlbum WHERE idUser=? AND idSong NOT IN (SELECT idSong FROM playlistsong where idPlaylist=?)";
		List<Song> songs=new ArrayList<Song>();
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idUser);
			pstatement.setInt(2, idPlaylist);

			result = pstatement.executeQuery();
			while (result.next()) {
				Song song = new Song();
				song.setIdSong(result.getInt("idSong"));
				song.setTitle(result.getString("title"));
				song.setIdAlbum(result.getInt("idAlbum"), con);
				//song.setAlbum(result.getString("nameAlbum"), result.getString("nameSinger"), result.getString("image"));
				songs.add(song);
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
		return songs;
	}
	
	
	public void updatePlaylistOrdered(int idPlaylist) throws SQLException {
		String query = "UPDATE playlist SET orderedByUser = 1 WHERE idPlaylist=?";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, idPlaylist);
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
