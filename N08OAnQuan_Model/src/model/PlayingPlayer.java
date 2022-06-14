package model;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayingPlayer implements Serializable {
	private int id;
	private int totalScore;
	private boolean isFirst;
	private Player player;
	private ArrayList<Capture> listCapture;

	public PlayingPlayer(Player p1) {
		super();
                this.listCapture = new ArrayList<>();
                player = p1;
	}

	public PlayingPlayer(int id, int totalScore, boolean isFirst, Player player) {
		super();
		this.id = id;
		this.totalScore = totalScore;
		this.isFirst = isFirst;
		this.player = player;
		this.listCapture = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ArrayList<Capture> getListCapture() {
		return listCapture;
	}

	public void setListCapture(ArrayList<Capture> listCapture) {
		this.listCapture = listCapture;
	}

}
