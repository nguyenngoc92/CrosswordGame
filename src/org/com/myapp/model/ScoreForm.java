package org.com.myapp.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScoreForm implements Serializable {

	private int matchId;
	private int point;
	private float time;

	public ScoreForm() {

	}

	public ScoreForm(int matchId, int point, float time) {
		this.matchId = matchId;
		this.point = point;
		this.time = time;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}

}
