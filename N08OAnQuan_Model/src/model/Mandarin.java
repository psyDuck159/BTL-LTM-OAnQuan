package model;

import java.io.Serializable;

public class Mandarin extends Square implements Serializable{
	private static final int INIT_SCORE_MANDARIN = 10;
	public Mandarin() {		
		super(INIT_SCORE_MANDARIN);
	}
}
