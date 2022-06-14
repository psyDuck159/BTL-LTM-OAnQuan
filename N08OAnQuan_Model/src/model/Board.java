package model;

import java.io.Serializable;

public class Board implements Serializable {
	private Square[] o;	

	public Board() {
		super();
		this.o = new Square[12];
                for(int i =0; i<=4; i++){
                    o[i] = new Ricefield();
                    o[i+6] = new Ricefield();
                }
                o[5] = new Mandarin();
		o[11] = new Mandarin();
	}

    public Square[] getO() {
        return o;
    }        

}
