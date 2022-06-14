package model;

import java.io.Serializable;

public class Capture implements Serializable {
	private int id;
	private int order;
	private int count;


	public Capture() {
		super();
	}

	public Capture(int id, int order, int count) {
		super();
		this.id = id;
		this.order = order;
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
