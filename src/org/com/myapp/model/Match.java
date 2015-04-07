package org.com.myapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Match implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2297080668276932816L;
	private int id;
	private String title;
	private int level;
	private int competition;

	private ArrayList<Item> items;

	public Match() {

	}

	public Match(int id, String title, int level, int competition,
			ArrayList<Item> items) {
		this.id = id;
		this.title = title;
		this.level = level;
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getCompetition() {
		return competition;
	}

	public void setCompetition(int competition) {
		this.competition = competition;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

}
