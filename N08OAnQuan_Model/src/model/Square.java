package model;

import java.io.Serializable;

public class Square implements Serializable{
	protected int count;

	public Square() {
		super();
	}

	public Square(int count) {
		super();
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}	
	
	
}
