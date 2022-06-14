package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Club implements Serializable {
	private int id;
	private String name;
	private String description;
	private Date createdDate;
	private boolean isPublic;
	private ArrayList<JoinedPlayer> listMember;
	private ArrayList<Tournament> listTournament;

	public Club() {
		super();
                listMember =  new ArrayList<>();
                listTournament =  new ArrayList<>();
	}

	public Club(int id, String name, String description, Date createdDate, boolean isPublic, ArrayList<JoinedPlayer> listMember, ArrayList<Tournament> listTour) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.createdDate = createdDate;
		this.isPublic = isPublic;
		this.listMember = listMember;
		this.listTournament = listTour;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public ArrayList<JoinedPlayer> getListMember() {
		return listMember;
	}

	public void setListMember(ArrayList<JoinedPlayer> listMember) {
		this.listMember = listMember;
	}

	public ArrayList<Tournament> getListTournament() {
		return listTournament;
	}

	public void setListTournament(ArrayList<Tournament> listTournament) {
		this.listTournament = listTournament;
	}
}
