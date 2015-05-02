package org.com.myapp.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties
public class MatchItemForm implements Serializable {

	private int matchId;
	private List<Integer> idList;

	public MatchItemForm() {

	}

	public MatchItemForm(int matchId, List<Integer> idList) {
		this.matchId = matchId;
		this.idList = idList;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public List<Integer> getIdList() {
		return idList;
	}

	public void setIdList(List<Integer> idList) {
		this.idList = idList;
	}

}
