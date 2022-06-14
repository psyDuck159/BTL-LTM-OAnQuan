package model;

import java.io.Serializable;
import java.util.Date;

public class AwardedPrize implements Serializable {
	private int id;
	private Date date;
	private Prize prize;
	
	public AwardedPrize() {
		super();
	}

	public AwardedPrize(int id, Date date, Prize prize) {
		super();
		this.id = id;
		this.date = date;
		this.prize = prize;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Prize getPrize() {
		return prize;
	}

	public void setPrize(Prize prize) {
		this.prize = prize;
	}
	
}
