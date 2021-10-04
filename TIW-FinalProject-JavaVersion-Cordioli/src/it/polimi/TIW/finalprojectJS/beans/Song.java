package it.polimi.TIW.finalprojectJS.beans;

import java.sql.Connection;
import java.sql.SQLException;

import it.polimi.TIW.finalprojectJS.dao.AlbumDAO;

public class Song {
	private int idSong;
	private String title;
	private int idAlbum;
	private int customOrder;
	private String genre;
	private String songPath;
	
	private Album album;
	
	public Song() {
		super();
	}
	public Song(int idSong, String title, int idAlbum, String genre, String songPath) {
		super();
		this.idSong = idSong;
		this.title = title;
		this.idAlbum = idAlbum;
		this.genre = genre;
		this.songPath = songPath;
	}
	
	public int getIdSong() {
		return idSong;
	}
	public void setIdSong(int idSong) {
		this.idSong = idSong;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIdAlbum() {
		return idAlbum;
	}
	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}
	public void setIdAlbum(int idAlbum, Connection conn) throws SQLException {
		this.idAlbum = idAlbum;
		AlbumDAO albumDAO = new AlbumDAO(conn);
		try {
			this.album = albumDAO.getAlbumWithId(idAlbum);
		}catch(SQLException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
	}
	public Album getAlbum() {
		return this.album;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getSongPath() {
		return songPath;
	}
	public void setSongPath(String songPath) {
		this.songPath = songPath;
	}
	public int getCustomOrder() {
		return customOrder;
	}
	public void setCustomOrder(int customOrder) {
		this.customOrder = customOrder;
	}
	@Override
    public boolean equals(Object obj) {
        if(obj==null)
            return false;
        if(obj==this)
            return true;
        if(!(obj instanceof Song))
            return false;
        return ((Song) obj).getIdSong() == idSong;
    }
	
	
}
