package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Tournament implements Serializable {
	private int id;
	private String name;
	private Date startDate;
	private Date endDate;
	private String description;
	private boolean isPublic;
	private Player creator;
	private ArrayList<Round> listRound;
	private ArrayList<AwardedPrize> listPrize;
	

	public Tournament() {
		super();
                listRound = new ArrayList<>();
                listPrize = new ArrayList<>();
	}

	public Tournament(int id, String name, Date startDate, Date endDate, String description, boolean isPublic,
			Player creator, ArrayList<Round> listRound, ArrayList<AwardedPrize> listPrize) {
		super();
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.isPublic = isPublic;
		this.creator = creator;
		this.listRound = listRound;
		this.listPrize = listPrize;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Player getCreator() {
		return creator;
	}

	public void setCreator(Player creator) {
		this.creator = creator;
	}

	public ArrayList<Round> getListRound() {
		return listRound;
	}

	public void setListRound(ArrayList<Round> listRound) {
		this.listRound = listRound;
	}

	public ArrayList<AwardedPrize> getListPrize() {
		return listPrize;
	}

	public void setListPrize(ArrayList<AwardedPrize> listPrize) {
		this.listPrize = listPrize;
	}

}
