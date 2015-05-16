package org.com.myapp.model;

import java.io.Serializable;

public class Word implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int index;

	private Position position;

	private ItemData item;

	public Word() {

	}

	public Word(int _order, Position _position, ItemData _item) {

		this.index = _order;
		this.position = _position;
		this.item = _item;
	}

	public String getAnswer() {
		return this.item.getAnswer();
	}

	public String getQuestion() {
		return this.item.getQuestion();
	}

	public int getRow() {
		return position.getR();
	}

	public int getCol() {
		return position.getC();
	}

	public int getMaxRow() {
		if (position.getDir() == Direction.ACROSS)
			return position.getR();
		else
			return position.getR() + item.getAnswer().length() - 1;
	}

	public int getMaxCol() {

		if (position.getDir() == Direction.ACROSS)
			return position.getC() + item.getAnswer().length() - 1;
		else
			return this.position.getC();
	}

	public Direction getDirection() {
		return this.position.getDir();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public ItemData getItem() {
		return item;
	}

	public void setItem(ItemData item) {
		this.item = item;
	}

}
