package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Round implements Serializable {
	private int id;
	private String name;
	private Date startDate;
	private Date endDate;
	private ArrayList<Match> listMatch;

	public Round() {
		super();
                listMatch = new ArrayList<>();
	}

	public Round(int id, String name, Date startDate, Date endDate, ArrayList<Match> listMatch) {
		super();
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.listMatch = listMatch;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ArrayList<Match> getListMatch() {
		return listMatch;
	}

	public void setListMatch(ArrayList<Match> listMatch) {
		this.listMatch = listMatch;
	}

}
