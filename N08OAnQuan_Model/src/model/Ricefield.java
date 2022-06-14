package model;

import java.io.Serializable;

public class Ricefield extends Square implements Serializable{
	private static final int INIT_SCORE_RICEFIELD = 5;
	public Ricefield() {
		super(INIT_SCORE_RICEFIELD);
	}		
}
