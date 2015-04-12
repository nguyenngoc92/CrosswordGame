package org.com.myapp.model;

import java.io.Serializable;

public class Item implements Serializable, Comparable<Item> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer idItem;

	private String question;
	private String suggest;
	private String answer;
	private boolean check;

	public Item() {

	}

	public Item(int idItem, String question, String suggest, String answer,
			boolean check) {
		this.idItem = idItem;
		this.question = question;
		this.suggest = suggest;
		this.answer = answer;
		this.check = check;
	}

	@Override
	public int compareTo(Item item) {

		return answer.length() - item.getAnswer().length();
	}

	public Integer getIdItem() {
		return idItem;
	}

	public void setIdItem(Integer idItem) {
		this.idItem = idItem;
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
