package org.com.myapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class MatchData implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private int idSubject;
	private int competition;
	private List<ItemData> items;


	public MatchData() {

	}

	public MatchData(int id, String title,  int idSubject, int competition,
			ArrayList<ItemData> items) {
		this.id = id;
		this.title = title;
		this.idSubject= idSubject;
		this.competition = competition;
		this.items = items;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	

	public int getIdSubject() {
		return idSubject;
	}

	public void setIdSubject(int idSubject) {
		this.idSubject = idSubject;
	}

	public int getCompetition() {
		return competition;
	}

	public void setCompetition(int competition) {
		this.competition = competition;
	}

	public List<ItemData> getItems() {
		return items;
	}

	public void setItems(List<ItemData> items) {
		this.items = items;
	}


}
