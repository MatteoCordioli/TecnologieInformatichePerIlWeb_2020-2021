package it.polimi.TIW.finalprojectJS.beans;

public class SongOrder {
	private int songId;
	private int songOrder;
	public SongOrder(int songId, int songOrder){
		this.songId=songId;
		this.songOrder=songOrder;
	}
	public int getSongOrder() {
		return songOrder;
	}
	public void setSongOrder(int songOrder) {
		this.songOrder = songOrder;
	}
	public int getSongId() {
		return songId;
	}
	public void setSongId(int songId) {
		this.songId = songId;
	}
	
}
