package model;

public enum Result {
	PLAYER1_WIN(0), 
	PLAYER2_WIN(1), 
	DRAW(2);
	private final int  value;
	Result(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
