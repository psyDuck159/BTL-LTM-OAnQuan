package model;

import java.io.Serializable;
import java.util.Date;

public class JoinedPlayer implements Serializable{
	private int id;
	private Date joinedDate;
	private String role;
	private Player player;
	
	public JoinedPlayer() {
		super();
	}

	public JoinedPlayer(int id, Date joinedDate, String role, Player player) {
		super();
		this.id = id;
		this.joinedDate = joinedDate;
		this.role = role;
		this.player = player;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	
}
