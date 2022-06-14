package model;

import java.io.Serializable;

public class Prize implements Serializable {
	private int id;
	private String name;
	private float price;
	private int point;

	public Prize() {
		super();
	}

	public Prize(int id, String name, float price, int point) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.point = point;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

}
