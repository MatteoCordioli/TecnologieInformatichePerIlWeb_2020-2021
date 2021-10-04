package it.polimi.TIW.finalproject.beans;


public class Album {
	private int idAlbum;
	private int idUser;
	private String nameAlbum;
	private String nameSinger;
	private int yearPublication;
	private String imagePath;
	
	public Album() {
		super();
	}
	
	public Album(int idUser, String nameAlbum, String nameSinger, int yearPublication, String imagePath) {
		super();
		this.idUser = idUser;
		this.nameAlbum = nameAlbum;
		this.nameSinger = nameSinger;
		this.yearPublication = yearPublication;
		this.imagePath = imagePath;
	}
	public int getIdAlbum() {
		return idAlbum;
	}

	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}
	
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getNameAlbum() {
		return nameAlbum;
	}
	public void setNameAlbum(String nameAlbum) {
		this.nameAlbum = nameAlbum;
	}
	public String getNameSinger() {
		return nameSinger;
	}
	public void setNameSinger(String nameSinger) {
		this.nameSinger = nameSinger;
	}
	public int getYearPublication() {
		return yearPublication;
	}
	public void setYearPublication(int yearPublication) {
		this.yearPublication = yearPublication;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getMainInfo() {
		return String.format("%-45s", nameAlbum).replace(' ', '-')+nameSinger;
		//String.format("%-45s", nameAlbum).replace(' ', '-')
	}
}
