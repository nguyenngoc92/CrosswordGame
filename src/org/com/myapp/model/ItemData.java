package org.com.myapp.model;

import java.io.Serializable;

public class ItemData implements Serializable, Comparable<ItemData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer idmatch;
	private Integer id;
	private String question;
	private String suggest;
	private String answer;

	private boolean check;

	public ItemData() {

	}

	public ItemData(Integer _id, String _question, String _suggest,
			String _answer, boolean _check) {
		this.id = _id;
		this.question = _question;
		this.suggest = _suggest;
		this.answer = _answer;
		this.check = _check;
	}

	@Override
	public int compareTo(ItemData item) {

		return answer.length() - item.getAnswer().length();
	}

	public Integer getIdmatch() {
		return idmatch;
	}

	public void setIdmatch(Integer idmatch) {
		this.idmatch = idmatch;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getSuggest() {
		return suggest;
	}

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}


}
