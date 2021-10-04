package it.polimi.TIW.finalproject.beans;

public class Playlist {
	private int idPlaylist;
	private String namePlaylist;
	private int idUser;
	private String date;
	
	public Playlist() {
		super();
	}

	public Playlist(String namePlaylist, int idUser, String date) {
		super();
		this.namePlaylist = namePlaylist;
		this.idUser = idUser;
		this.date = date;
	}
	
	public Playlist(int idPlaylist, String namePlaylist, int idUser, String date) {
		super();
		this.idPlaylist = idPlaylist;
		this.namePlaylist = namePlaylist;
		this.idUser = idUser;
		this.date = date;
	}
	
	public int getIdPlaylist() {
		return idPlaylist;
	}
	public void setIdPlaylist(int idPlaylist) {
		this.idPlaylist = idPlaylist;
	}
	public String getNamePlaylist() {
		return namePlaylist;
	}
	public void setNamePlaylist(String namePlaylist) {
		this.namePlaylist = namePlaylist;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
